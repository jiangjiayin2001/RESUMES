package com.resumestudio.storage;

public interface ImageStorageService {
    StoredImage store(byte[] bytes, String extension, String contentType);
    StoredImageContent read(String key);
}
