package com.example.lesson3.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {

    private static final Set<String> ALLOWED_TYPES = Set.of(
        "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    public static String saveFile(String uploadDir, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IOException("Chỉ chấp nhận file ảnh (JPEG, PNG, GIF, WebP).");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            originalName = "file";
        }

        // Lấy phần extension, loại bỏ path traversal và ký tự đặc biệt
        String safeName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
        // Ngăn path traversal: chỉ lấy tên file, bỏ các thư mục phía trước
        int lastSlash = safeName.lastIndexOf('/');
        if (lastSlash >= 0) safeName = safeName.substring(lastSlash + 1);
        lastSlash = safeName.lastIndexOf('\\');
        if (lastSlash >= 0) safeName = safeName.substring(lastSlash + 1);

        String filename = System.currentTimeMillis() + "_" + safeName;
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(filename);
            // Kiểm tra file đích nằm trong thư mục upload (chống path traversal)
            if (!filePath.normalize().startsWith(uploadPath.normalize())) {
                throw new IOException("Đường dẫn file không hợp lệ");
            }
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        return filename;
    }

    public static void deleteFile(String uploadDir, String filename) throws IOException {
        if (filename == null || filename.isEmpty()) {
            return;
        }

        Path filePath = Paths.get(uploadDir).resolve(filename);
        // Kiểm tra file đích nằm trong thư mục upload
        if (!filePath.normalize().startsWith(Paths.get(uploadDir).normalize())) {
            return;
        }
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }
}
