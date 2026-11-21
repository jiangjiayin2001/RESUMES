package com.resumestudio.storage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.resumestudio.common.ResourceNotFoundException;
import com.resumestudio.config.properties.AppProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.storage.type", havingValue = "oss")
public class OssImageStorageService implements ImageStorageService {

    private final AppProperties appProperties;
    private OSS client;

    @PostConstruct
    void initialize() {
        AppProperties.Oss oss = appProperties.getStorage().getOss();
        if (isBlank(oss.getEndpoint()) || isBlank(oss.getBucket()) || isBlank(oss.getAccessKeyId())
                || isBlank(oss.getAccessKeySecret()) || isBlank(oss.getPublicBaseUrl())) {
            throw new IllegalStateException("OSS storage is enabled but its configuration is incomplete");
        }
        client = new OSSClientBuilder().build(
                oss.getEndpoint(),
                oss.getAccessKeyId(),
                oss.getAccessKeySecret()
        );
    }

    @Override
    public StoredImage store(byte[] bytes, String extension, String contentType) {
        AppProperties.Oss oss = appProperties.getStorage().getOss();
        String keyPrefix = normalizePrefix(oss.getKeyPrefix());
        String key = keyPrefix + UUID.randomUUID() + "." + extension;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);
        metadata.setContentType(contentType);
        metadata.setCacheControl("public,max-age=31536000,immutable");
        client.putObject(oss.getBucket(), key, new ByteArrayInputStream(bytes), metadata);
        return new StoredImage(
                key,
                oss.getPublicBaseUrl().replaceAll("/+$", "") + "/" + key,
                contentType,
                "oss"
        );
    }

    @Override
    public StoredImageContent read(String key) {
        AppProperties.Oss oss = appProperties.getStorage().getOss();
        try {
            var object = client.getObject(oss.getBucket(), key);
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

    private String normalizePrefix(String prefix) {
        if (isBlank(prefix)) return "";
        String normalized = prefix.replaceAll("^/+", "");
        return normalized.endsWith("/") ? normalized : normalized + "/";
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
