package com.worfwint.tabletoprpgmanager.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.worfwint.tabletoprpgmanager.common.exception.BadRequestException;
import com.worfwint.tabletoprpgmanager.common.exception.TabletopRpgManagerException;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.core.sync.RequestBody;

@Service
public class S3StorageService {

    private final S3Client s3Client;
    private final S3Properties properties;

    public S3StorageService(S3Client s3Client, S3Properties properties) {
        this.s3Client = s3Client;
        this.properties = properties;
    }

    public String uploadNewsImage(Long articleId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Image file is required");
        }
        if (isBlank(properties.accessKey()) || isBlank(properties.secretKey())) {
            throw new TabletopRpgManagerException("S3 credentials are not configured");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new BadRequestException("Only image files are allowed");
        }

        String extension = resolveExtension(file.getOriginalFilename(), contentType);
        String key = "news/" + articleId + "/" + UUID.randomUUID() + extension;

        PutObjectRequest.Builder requestBuilder = PutObjectRequest.builder()
                .bucket(properties.bucket())
                .key(key)
                .contentType(contentType);
        if (properties.publicRead()) {
            requestBuilder.acl(ObjectCannedACL.PUBLIC_READ);
        }

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(requestBuilder.build(),
                    RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (IOException | S3Exception ex) {
            throw new TabletopRpgManagerException("Failed to upload image", ex);
        }

        return buildPublicUrl(key);
    }

    public void deleteByPublicUrl(String publicUrl) {
        String key = extractKey(publicUrl);
        if (key == null || key.isBlank()) {
            return;
        }
        try {
            s3Client.deleteObject(builder -> builder.bucket(properties.bucket()).key(key));
        } catch (S3Exception ex) {
            throw new TabletopRpgManagerException("Failed to delete image", ex);
        }
    }

    private String buildPublicUrl(String key) {
        String baseUrl = properties.publicBaseUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl + "/" + key;
    }

    private String extractKey(String publicUrl) {
        if (publicUrl == null) {
            return null;
        }
        String baseUrl = properties.publicBaseUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        if (!publicUrl.startsWith(baseUrl + "/")) {
            return null;
        }
        return publicUrl.substring(baseUrl.length() + 1);
    }

    private String resolveExtension(String originalFilename, String contentType) {
        if (originalFilename != null) {
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
                String extension = originalFilename.substring(dotIndex).toLowerCase(Locale.ROOT);
                if (extension.matches("\\.[a-z0-9]{1,5}")) {
                    return extension;
                }
            }
        }
        return defaultExtension(contentType);
    }

    private String defaultExtension(String contentType) {
        if (contentType == null) {
            return "";
        }
        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            case "image/bmp" -> ".bmp";
            case "image/svg+xml" -> ".svg";
            default -> "";
        };
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
