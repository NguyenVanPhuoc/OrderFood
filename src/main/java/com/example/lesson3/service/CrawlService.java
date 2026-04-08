package com.example.lesson3.service;

import com.example.lesson3.model.Product;
import com.example.lesson3.model.Store;
import com.example.lesson3.repository.ProductRepository;
import com.example.lesson3.repository.StoreRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class CrawlService {

    private static final Logger log = LoggerFactory.getLogger(CrawlService.class);

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final int MAX_NAME_LENGTH = 255;
    private static final int MAX_DESCRIPTION_LENGTH = 1000;
    private static final int MAX_IMAGE_URL_LENGTH = 500;

    private static final String GRABFOOD_GUEST_API = "https://portal.grab.com/foodweb/guest/v2";

    private static final String[] PRICE_FIELDS = {
        "priceInMinorUnit",   // GrabFood API thực tế (VND, không có cents)
        "price", "priceInCents", "originalPrice", "discountedPrice", "priceForDisplay", "minPrice"
    };
    private static final String[] IMAGE_FIELDS = {
        "imgHref",            // GrabFood API thực tế
        "imgUrl", "image", "photoUrl", "thumbnailUrl", "photo", "imageUrl", "coverImgUrl"
    };

    public CrawlService(ProductRepository productRepository, StoreRepository storeRepository) {
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
    }

@Transactional
    public List<Product> crawlGrabFoodMenu(Long storeId, String inputUrlOrCode) throws IOException {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found: " + storeId));

        try {
            productRepository.deleteByStore_Id(storeId);
        } catch (Exception e) {
            log.warn("Lỗi khi xóa sản phẩm cũ của store {}: {}", storeId, e.getMessage());
        }

        String fullUrl = normalizeToFullUrl(inputUrlOrCode);
        log.info("Crawling URL: {}", fullUrl);

        String merchantId = extractMerchantId(fullUrl);
        log.info("merchantID: {}", merchantId);

        // Strategy 1: Playwright — dùng Chrome thật để load trang + intercept API response
        List<Product> products = fetchWithPlaywright(fullUrl, merchantId, store);
        log.info("Playwright: {} sản phẩm", products.size());

        // Strategy 2: Jsoup parse HTML đã render (nếu Playwright không intercept được API)
        if (products.isEmpty()) {
            try {
                Document doc = fetchDocument(fullUrl);
                products = parseNextData(doc, store);
                log.info("__NEXT_DATA__: {} sản phẩm", products.size());
                if (products.isEmpty()) {
                    products = parseHtml(doc, store);
                    log.info("HTML selectors: {} sản phẩm", products.size());
                }
            } catch (IOException e) {
                log.warn("Không fetch được HTML: {}", e.getMessage());
            }
        }

        if (products.isEmpty()) {
            throw new IOException(
                "Không thể lấy dữ liệu sản phẩm từ GrabFood. " +
                (merchantId == null
                    ? "Không tìm thấy merchantID trong URL."
                    : "Đã thử tất cả phương án (Playwright, API, HTML) nhưng không lấy được dữ liệu.")
            );
        }

        List<Product> validated = validateAndNormalizeProducts(products);
        log.info("Sau validate: {} sản phẩm hợp lệ", validated.size());

        try {
            List<Product> saved = productRepository.saveAll(validated);
            log.info("Đã lưu {} sản phẩm cho storeId={}", saved.size(), storeId);
            return saved;
        } catch (Exception e) {
            log.error("Lỗi khi lưu sản phẩm: {}", e.getMessage(), e);
            throw new RuntimeException("Could not save products to database", e);
        }
    }

    // =========================================================
    // Strategy 1: Playwright — chạy Chrome thật, intercept API
    // =========================================================

    /**
     * Dùng Playwright với Chrome đã cài sẵn (setChannel("chrome")):
     * - Load trang nhà hàng GrabFood
     * - Lắng nghe response từ GrabFood guest API
     * - Parse JSON trả về → danh sách sản phẩm
     * Nếu API không intercept được, fallback parse HTML đã render.
     */
    private List<Product> fetchWithPlaywright(String restaurantUrl, String merchantId, Store store) {
        if (restaurantUrl == null) return Collections.emptyList();

        try (Playwright playwright = Playwright.create()) {
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                    .setHeadless(true);
                    // Dùng Chromium của Playwright (tự download khi @PostConstruct chạy)

            try (Browser browser = playwright.chromium().launch(launchOptions)) {
                Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                        .setLocale("vi-VN")
                        .setTimezoneId("Asia/Ho_Chi_Minh")
                        // Geolocation Đà Nẵng — khớp với latlng GrabFood gửi lên API
                        .setGeolocation(new com.microsoft.playwright.options.Geolocation(
                                16.064674213809425, 108.22397998878569))
                        .setPermissions(List.of("geolocation"))
                        .setExtraHTTPHeaders(Map.of(
                                "X-Country-Code", "VN",
                                "X-Gfc-Country", "VN"
                        ));

                try (BrowserContext context = browser.newContext(contextOptions)) {
                    Page page = context.newPage();

                    // Dùng waitForResponse để chờ chính xác API call của GrabFood
                    // Tránh race condition khi dùng onResponse listener thụ động
                    String apiPattern = "/foodweb/guest/v2/merchants/";
                    com.microsoft.playwright.Response[] apiResponse = {null};

                    log.info("Playwright navigating to: {}", restaurantUrl);
                    try {
                        // navigate() và waitForResponse() chạy song song:
                        // - navigate bắt đầu load trang
                        // - waitForResponse chờ đúng API call xuất hiện trong lúc trang load
                        apiResponse[0] = page.waitForResponse(
                            response -> response.url().contains(apiPattern)
                                && (merchantId == null || response.url().contains(merchantId))
                                && response.status() == 200,
                            new Page.WaitForResponseOptions().setTimeout(180000),
                            () -> page.navigate(restaurantUrl,
                                    new Page.NavigateOptions().setTimeout(180000))
                        );
                    } catch (Exception e) {
                        log.warn("waitForResponse timeout hoặc lỗi: {}", e.getMessage());
                    }

                    // Parse JSON từ API response đã chờ được
                    if (apiResponse[0] != null) {
                        try {
                            String json = apiResponse[0].text();
                            log.info("Intercepted GrabFood API: {}", apiResponse[0].url());
                            // Log 2000 ký tự đầu để xem cấu trúc JSON thực tế
                            log.info("GrabFood API JSON (2000 chars): {}",
                                    json.length() > 2000 ? json.substring(0, 2000) : json);
                            List<Product> products = parseJsonString(json, store);
                            if (!products.isEmpty()) return products;
                        } catch (Exception e) {
                            log.warn("Không đọc được API response: {}", e.getMessage());
                        }
                    }

                    // Fallback: parse HTML đã render bởi React
                    log.info("Fallback: parse rendered HTML từ Playwright");
                    String html = page.content();
                    Document doc = Jsoup.parse(html);
                    List<Product> fromHtml = parseHtml(doc, store);
                    if (!fromHtml.isEmpty()) return fromHtml;

                    return parseNextData(doc, store);
                }
            }
        } catch (Exception e) {
            log.warn("Playwright thất bại: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<Product> parseJsonString(String json, Store store) {
        try {
            JsonNode root = objectMapper.readTree(json);
            List<Product> products = new ArrayList<>();
            extractProductsFromJson(root, store, products, 0);
            return products;
        } catch (Exception e) {
            log.warn("Lỗi parse JSON: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    // =========================================================
    // Strategy 2a: __NEXT_DATA__ SSR JSON
    // =========================================================

    private Document fetchDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "vi-VN,vi;q=0.9,en;q=0.8")
                .referrer("https://food.grab.com/")
                .timeout(30000)
                .ignoreContentType(true)
                .get();
    }

    private List<Product> parseNextData(Document doc, Store store) {
        Element nextDataScript = doc.getElementById("__NEXT_DATA__");
        if (nextDataScript == null) return Collections.emptyList();

        String jsonText = nextDataScript.data();
        if (jsonText == null || jsonText.trim().isEmpty()) return Collections.emptyList();

        return parseJsonString(jsonText, store);
    }

    // =========================================================
    // Strategy 2b: HTML selectors (rendered DOM)
    // =========================================================

    private List<Product> parseHtml(Document doc, Store store) {
        List<Product> products = new ArrayList<>();

        Elements productElements = doc.select("div[class*=menuItemWrapper]");
        if (productElements.isEmpty()) {
            productElements = doc.select("[data-selenium=menu-item], [data-testid=menu-item]");
        }
        if (productElements.isEmpty()) {
            productElements = doc.select("div:has(h3, h4):has(span:containsOwn(₫))");
        }

        log.debug("HTML selector tìm thấy {} elements", productElements.size());

        for (Element el : productElements) {
            try {
                String name = selectText(el,
                        "div[class*=itemName]", "p[class*=itemName]", "p[class*=itemNameTitle]",
                        "h3", "h4");
                String priceText = selectText(el,
                        "p[class*=discountedPrice]", "div[class*=discountedPrice]",
                        "div[class*=price]", "span[class*=price]");
                String description = selectText(el,
                        "p[class*=itemDescription]", "div[class*=itemDescription]");

                String image = null;
                Element imgEl = el.selectFirst("img[class*=realImage]");
                if (imgEl == null) imgEl = el.selectFirst("img[class*=itemPhoto]");
                if (imgEl != null) image = imgEl.attr("src");

                if (name != null && priceText != null) {
                    double price = parseVnCurrency(priceText);
                    if (price > 0) {
                        Product p = new Product();
                        p.setName(name.trim());
                        p.setPrice(price);
                        p.setDescription(description != null ? description.trim() : "");
                        p.setImage(image != null ? image : "");
                        p.setStatus(el.selectFirst("[class*=menuItem--disable]") != null ? 2 : 1);
                        p.setStore(store);
                        products.add(p);
                    }
                }
            } catch (Exception e) {
                log.debug("Lỗi parse element: {}", e.getMessage());
            }
        }
        return products;
    }

    private String selectText(Element parent, String... selectors) {
        for (String selector : selectors) {
            Element found = parent.selectFirst(selector);
            if (found != null && !found.text().trim().isEmpty()) {
                return found.text().trim();
            }
        }
        return null;
    }

    // =========================================================
    // JSON recursive parser
    // =========================================================

    private void extractProductsFromJson(JsonNode node, Store store, List<Product> products, int depth) {
        if (depth > 20 || node == null || node.isNull() || node.isMissingNode()) return;

        if (node.isObject()) {
            if (looksLikeProduct(node)) {
                Product p = buildProductFromJson(node, store);
                if (p != null) products.add(p);
                return;
            }
            node.fields().forEachRemaining(entry ->
                    extractProductsFromJson(entry.getValue(), store, products, depth + 1));
        } else if (node.isArray()) {
            for (JsonNode item : node) {
                extractProductsFromJson(item, store, products, depth + 1);
            }
        }
    }

    private boolean looksLikeProduct(JsonNode node) {
        JsonNode nameNode = node.get("name");
        if (nameNode == null || !nameNode.isTextual()) return false;
        String name = nameNode.asText().trim();
        if (name.isEmpty() || name.length() > MAX_NAME_LENGTH) return false;

        for (String field : PRICE_FIELDS) {
            JsonNode priceNode = node.get(field);
            if (priceNode != null && priceNode.isNumber() && priceNode.asDouble() > 0) {
                return true;
            }
        }
        return false;
    }

    private Product buildProductFromJson(JsonNode node, Store store) {
        String name = node.get("name").asText().trim();

        double price = 0;
        for (String field : PRICE_FIELDS) {
            JsonNode priceNode = node.get(field);
            if (priceNode != null && priceNode.isNumber() && priceNode.asDouble() > 0) {
                price = priceNode.asDouble();
                break;
            }
        }
        if (price <= 0) return null;

        String description = "";
        JsonNode descNode = node.get("description");
        if (descNode != null && descNode.isTextual()) {
            description = descNode.asText().trim();
        }

        String image = extractImageUrl(node);

        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setDescription(description);
        p.setImage(image);
        p.setStatus(1);
        p.setStore(store);
        return p;
    }

    private String extractImageUrl(JsonNode node) {
        for (String field : IMAGE_FIELDS) {
            JsonNode imgNode = node.get(field);
            if (imgNode == null) continue;
            if (imgNode.isTextual() && !imgNode.asText().trim().isEmpty()) {
                return imgNode.asText().trim();
            }
            if (imgNode.isObject()) {
                for (String sub : new String[]{"value", "url", "src"}) {
                    JsonNode subNode = imgNode.get(sub);
                    if (subNode != null && subNode.isTextual() && !subNode.asText().trim().isEmpty()) {
                        return subNode.asText().trim();
                    }
                }
            }
        }
        return "";
    }

    // =========================================================
    // Helpers
    // =========================================================

    String extractMerchantId(String url) {
        if (url == null) return null;
        try {
            String path = url.split("\\?")[0].split("#")[0];
            while (path.endsWith("/")) path = path.substring(0, path.length() - 1);
            if (path.endsWith("/items")) path = path.substring(0, path.lastIndexOf("/items"));
            String[] segments = path.split("/");
            if (segments.length > 0) {
                String last = segments[segments.length - 1];
                if (last.matches("[0-9A-Za-z\\-]+") && last.length() > 3) {
                    return last;
                }
            }
        } catch (Exception e) {
            log.debug("Không extract được merchantID: {}", url);
        }
        return null;
    }

    private List<Product> validateAndNormalizeProducts(List<Product> products) {
        List<Product> validated = new ArrayList<>();
        Set<String> seenNames = new HashSet<>();

        for (Product product : products) {
            try {
                Product vp = validateAndNormalizeProduct(product);
                if (vp != null) {
                    String nameKey = vp.getName().trim().toLowerCase();
                    if (seenNames.add(nameKey)) {
                        validated.add(vp);
                    }
                }
            } catch (Exception e) {
                log.warn("Lỗi validate sản phẩm: {}", e.getMessage());
            }
        }
        return validated;
    }

    private Product validateAndNormalizeProduct(Product product) {
        if (product == null) return null;
        if (product.getName() == null || product.getName().trim().isEmpty()) return null;

        String name = product.getName().trim();
        if (name.length() > MAX_NAME_LENGTH) name = name.substring(0, MAX_NAME_LENGTH);
        product.setName(name);

        if (product.getPrice() == null || product.getPrice() <= 0) return null;

        if (product.getDescription() != null) {
            String desc = product.getDescription().trim();
            if (desc.length() > MAX_DESCRIPTION_LENGTH) desc = desc.substring(0, MAX_DESCRIPTION_LENGTH);
            product.setDescription(desc);
        } else {
            product.setDescription("");
        }

        if (product.getImage() != null) {
            String img = product.getImage().trim();
            if (img.length() > MAX_IMAGE_URL_LENGTH) img = img.substring(0, MAX_IMAGE_URL_LENGTH);
            product.setImage(img);
        } else {
            product.setImage("");
        }

        if (product.getStore() == null) return null;
        return product;
    }

    private String normalizeToFullUrl(String input) {
        if (input == null) return null;
        if (input.startsWith("http")) return input;

        try {
            String decoded = URLDecoder.decode(input, StandardCharsets.UTF_8.name());
            if (decoded.startsWith("http")) return decoded;
        } catch (Exception e) {
            log.debug("Không decode được URL: {}", input);
        }

        return "https://food.grab.com/vn/vi/restaurant/" + input;
    }

    private double parseVnCurrency(String priceText) {
        if (priceText == null) return 0;
        String cleaned = priceText.replaceAll("[^0-9]", "");
        if (cleaned.isEmpty()) return 0;
        return Double.parseDouble(cleaned);
    }
}
