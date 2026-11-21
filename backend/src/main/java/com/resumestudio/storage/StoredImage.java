package com.resumestudio.storage;

public record StoredImage(String key, String url, String contentType, String provider) {
}
