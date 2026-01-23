package com.worfwint.tabletoprpgmanager.storage;

import java.net.URI;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@EnableConfigurationProperties(S3Properties.class)
public class S3Config {

    @Bean
    public S3Client s3Client(S3Properties properties) {
        String endpoint = normalizeEndpoint(properties.endpoint(), properties.bucket());
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(properties.accessKey(), properties.secretKey())))
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(properties.region()))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    private String normalizeEndpoint(String endpoint, String bucket) {
        if (endpoint == null || endpoint.isBlank()) {
            return endpoint;
        }
        String normalized = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
        String bucketSuffix = "/" + bucket;
        if (bucket != null && !bucket.isBlank() && normalized.endsWith(bucketSuffix)) {
            return normalized.substring(0, normalized.length() - bucketSuffix.length());
        }
        return normalized;
    }
}
