package com.resumestudio.storage;

import com.resumestudio.common.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@ConditionalOnProperty(name = "app.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalImageStorageService implements ImageStorageService {

    private static final Pattern SAFE_KEY = Pattern.compile("^[a-f0-9-]{36}\\.(jpg|png|webp)$");

    @Value("${app.storage.local.root:./data/uploads}")
    private String rootPath;

    private Path root;

    @PostConstruct
    void initialize() {
        try {
            root = Path.of(rootPath).toAbsolutePath().normalize();
            Files.createDirectories(root);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to initialize local image storage", exception);
        }
    }

    @Override
    public StoredImage store(byte[] bytes, String extension, String contentType) {
        String key = UUID.randomUUID() + "." + extension;
        Path target = root.resolve(key).normalize();
        if (!root.equals(target.getParent())) {
            throw new IllegalArgumentException("Invalid storage key");
        }
        try {
            Files.write(target, bytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save image", exception);
        }
        return new StoredImage(key, "/api/public/uploads/" + key, contentType, "local");
    }

    @Override
    public StoredImageContent read(String key) {
        if (key == null || !SAFE_KEY.matcher(key).matches()) {
            throw new ResourceNotFoundException("图片不存在");
        }
        Path target = root.resolve(key).normalize();
        if (!root.equals(target.getParent()) || !Files.isRegularFile(target)) {
            throw new ResourceNotFoundException("图片不存在");
        }
        try {
            String contentType = key.endsWith(".png") ? "image/png"
                    : key.endsWith(".webp") ? "image/webp" : "image/jpeg";
            return new StoredImageContent(Files.readAllBytes(target), contentType);
        } catch (IOException exception) {
            throw new ResourceNotFoundException("图片不存在");
        }
    }
}
