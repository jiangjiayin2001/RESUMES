package com.resumestudio.storage;

import com.resumestudio.common.ResourceNotFoundException;
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

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageUploadService uploadService;
    private final ImageStorageService storageService;

    @PostMapping(value = "/api/uploads/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageUploadResponse upload(@RequestPart("file") MultipartFile file) {
        StoredImage image = uploadService.upload(file);
        return new ImageUploadResponse(image.url(), image.provider());
    }

    @GetMapping("/api/public/uploads/{key}")
    public ResponseEntity<byte[]> image(@PathVariable String key) {
        StoredImageContent image = storageService.read(key);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.contentType()))
                .cacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic().immutable())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().build().toString())
                .header("X-Content-Type-Options", "nosniff")
                .body(image.bytes());
    }

    public record ImageUploadResponse(String url, String provider) {
    }
}
