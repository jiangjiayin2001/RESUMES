package com.resumestudio.storage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.resumestudio.common.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "app.storage.type", havingValue = "oss")
public class OssImageStorageService implements ImageStorageService {

    @Value("${app.storage.oss.endpoint}") private String endpoint;
    @Value("${app.storage.oss.bucket}") private String bucket;
    @Value("${app.storage.oss.access-key-id}") private String accessKeyId;
    @Value("${app.storage.oss.access-key-secret}") private String accessKeySecret;
    @Value("${app.storage.oss.public-base-url}") private String publicBaseUrl;

    private OSS client;

    @PostConstruct
    void initialize() {
        if (endpoint.isBlank() || bucket.isBlank() || accessKeyId.isBlank()
                || accessKeySecret.isBlank() || publicBaseUrl.isBlank()) {
            throw new IllegalStateException("OSS storage is enabled but its configuration is incomplete");
        }
        client = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    @Override
    public StoredImage store(byte[] bytes, String extension, String contentType) {
        String key = "resume/avatar/" + UUID.randomUUID() + "." + extension;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);
        metadata.setContentType(contentType);
        metadata.setCacheControl("public,max-age=31536000,immutable");
        client.putObject(bucket, key, new ByteArrayInputStream(bytes), metadata);
        return new StoredImage(
                key,
                publicBaseUrl.replaceAll("/+$", "") + "/" + key,
                contentType,
                "oss"
        );
    }

    @Override
    public StoredImageContent read(String key) {
        try {
            var object = client.getObject(bucket, key);
            if (object == null) {
                throw new ResourceNotFoundException("图片不存在");
            }
            return new StoredImageContent(object.getObjectContent().readAllBytes(), "application/octet-stream");
        } catch (IOException exception) {
            throw new ResourceNotFoundException("图片不存在");
        }
    }

    @PreDestroy
    void shutdown() {
        if (client != null) {
            client.shutdown();
        }
    }
}
