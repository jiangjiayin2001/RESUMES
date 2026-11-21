package com.resumestudio.storage;

import com.resumestudio.common.ApiPaths;
import com.resumestudio.common.ResourceNotFoundException;
import com.resumestudio.config.properties.AppProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalImageStorageService implements ImageStorageService {

    private final AppProperties appProperties;

    private Path root;
    private Pattern safeKeyPattern;

    @PostConstruct
    void initialize() {
        AppProperties.Storage storage = appProperties.getStorage();
        try {
            root = Path.of(storage.getLocal().getRoot()).toAbsolutePath().normalize();
            Files.createDirectories(root);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to initialize local image storage", exception);
        }
        Set<String> formats = storage.getAllowedFormats().stream()
                .map(value -> value.toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
        String formatPattern = formats.stream().map(Pattern::quote).collect(Collectors.joining("|"));
        safeKeyPattern = Pattern.compile("^[a-f0-9-]{36}\\.(?:" + formatPattern + ")$");
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
        return new StoredImage(key, ApiPaths.PUBLIC_UPLOADS + "/" + key, contentType, "local");
    }

    @Override
    public StoredImageContent read(String key) {
        if (key == null || !safeKeyPattern.matcher(key).matches()) {
            throw new ResourceNotFoundException("图片不存在");
        }
        Path target = root.resolve(key).normalize();
        if (!root.equals(target.getParent()) || !Files.isRegularFile(target)) {
            throw new ResourceNotFoundException("图片不存在");
        }
        try {
            return new StoredImageContent(Files.readAllBytes(target), contentType(key));
        } catch (IOException exception) {
            throw new ResourceNotFoundException("图片不存在");
        }
    }

    private String contentType(String key) {
        if (key.endsWith(".png")) return "image/png";
        if (key.endsWith(".webp")) return "image/webp";
        return "image/jpeg";
    }
}


