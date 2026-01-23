package com.worfwint.tabletoprpgmanager.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "app.s3")
public record S3Properties(
        @NotBlank String endpoint,
        @NotBlank String bucket,
        @NotBlank String publicBaseUrl,
        @NotBlank String region,
        String accessKey,
        String secretKey,
        boolean publicRead
) {}
