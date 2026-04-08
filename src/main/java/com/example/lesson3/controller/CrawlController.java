package com.example.lesson3.controller;

import com.example.lesson3.model.Product;
import com.example.lesson3.service.CrawlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/crawl")
public class CrawlController {

    private static final Logger log = LoggerFactory.getLogger(CrawlController.class);

    private final CrawlService crawlService;

    public CrawlController(CrawlService crawlService) {
        this.crawlService = crawlService;
    }

    @PostMapping("/store")
    public ResponseEntity<?> crawl(@RequestBody Map<String, String> body) {
        try {
            Long storeId = Long.valueOf(body.get("storeId"));
            String url = body.get("url");
            List<Product> products = crawlService.crawlGrabFoodMenu(storeId, url);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Crawl dữ liệu thành công");
            response.put("productsCount", products.size());
            log.info("Crawl thành công storeId={}, {} sản phẩm", storeId, products.size());

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.warn("Crawl thất bại (IOException): {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(502).body(errorResponse);
        } catch (Exception e) {
            log.error("Crawl thất bại (Exception): {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
