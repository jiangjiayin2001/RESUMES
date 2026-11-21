package com.resumestudio.storage;

import com.resumestudio.common.ApiPaths;
import com.resumestudio.config.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageUploadService uploadService;
    private final ImageStorageService storageService;
    private final AppProperties appProperties;

    @PostMapping(value = ApiPaths.UPLOAD_IMAGES, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageUploadResponse upload(@RequestPart("file") MultipartFile file) {
        StoredImage image = uploadService.upload(file);
        return new ImageUploadResponse(image.url(), image.provider());
    }

    @GetMapping(ApiPaths.UPLOAD_CONFIG)
    public ImageUploadConfigResponse uploadConfig() {
        AppProperties.Storage storage = appProperties.getStorage();
        return new ImageUploadConfigResponse(
                storage.getMaxImageBytes(),
                storage.getMaxImageDimension(),
                List.copyOf(storage.getAllowedFormats()),
                List.copyOf(storage.getAllowedContentTypes()),
                storage.getType()
        );
    }

    @GetMapping(ApiPaths.PUBLIC_UPLOADS + "/{key}")
    public ResponseEntity<byte[]> image(@PathVariable String key) {
        StoredImageContent image = storageService.read(key);
        int cacheDays = appProperties.getStorage().getImageCacheMaxAgeDays();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.contentType()))
                .cacheControl(CacheControl.maxAge(Duration.ofDays(cacheDays)).cachePublic().immutable())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().build().toString())
                .header("X-Content-Type-Options", "nosniff")
                .body(image.bytes());
    }

    public record ImageUploadResponse(String url, String provider) {
    }

    public record ImageUploadConfigResponse(
            long maxImageBytes,
            int maxImageDimension,
            List<String> allowedFormats,
            List<String> allowedContentTypes,
            String provider
    ) {
    }
}
