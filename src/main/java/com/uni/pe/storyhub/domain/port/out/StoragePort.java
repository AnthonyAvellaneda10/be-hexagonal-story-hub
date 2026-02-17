package com.uni.pe.storyhub.domain.port.out;

import java.io.InputStream;

public interface StoragePort {
    void uploadFile(String key, InputStream inputStream, long contentLength, String contentType);

    String generatePresignedUrl(String key, int durationInMinutes);

    void deleteFile(String key);
}
