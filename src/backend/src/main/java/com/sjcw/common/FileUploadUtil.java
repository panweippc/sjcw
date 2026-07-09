package com.sjcw.common;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FileUploadUtil {

    private static final String UPLOAD_DIR_NAME = "uploads";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private static Path getUploadBasePath() {
        return Paths.get(System.getProperty("user.dir"), UPLOAD_DIR_NAME).toAbsolutePath().normalize();
    }

    public static String upload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new RuntimeException("文件名不能为空");
        }

        String extension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;
        String datePath = LocalDateTime.now().format(DATE_FORMATTER);
        String relativePath = datePath + "/" + newFilename;
        
        Path basePath = getUploadBasePath();
        Path filePath = basePath.resolve(datePath);

        if (!Files.exists(filePath)) {
            Files.createDirectories(filePath);
        }

        File destFile = filePath.resolve(newFilename).toFile();
        file.transferTo(destFile);

        return "/uploads/" + relativePath;
    }

    public static boolean delete(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        Path basePath = getUploadBasePath();
        Path path = basePath.resolve(filePath.replace("/uploads/", ""));
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            return false;
        }
    }

    public static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    public static boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.startsWith("image/"));
    }

    public static void initUploadDir() {
        try {
            Path uploadPath = getUploadBasePath();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("创建上传目录失败", e);
        }
    }
}