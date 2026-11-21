package com.resumestudio.storage;

import com.resumestudio.common.BadRequestException;
import com.resumestudio.config.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final ImageStorageService storageService;
    private final AppProperties appProperties;

    public StoredImage upload(MultipartFile file) {
        AppProperties.Storage storage = appProperties.getStorage();
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("请选择要上传的图片");
        }
        if (file.getSize() > storage.getMaxImageBytes()) {
            throw new BadRequestException("图片超过配置的大小限制");
        }

        try {
            byte[] bytes = file.getBytes();
            ImageFormat format = detect(bytes);
            Set<String> allowedFormats = storage.getAllowedFormats().stream()
                    .map(value -> value.toLowerCase(Locale.ROOT))
                    .collect(Collectors.toUnmodifiableSet());
            if (!allowedFormats.contains(format.extension())) {
                throw new BadRequestException("图片格式不在允许范围内");
            }
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            int maxDimension = storage.getMaxImageDimension();
            if (image != null && (image.getWidth() > maxDimension || image.getHeight() > maxDimension)) {
                throw new BadRequestException("图片尺寸超过配置限制");
            }
            return storageService.store(bytes, format.extension(), format.contentType());
        } catch (IOException exception) {
            throw new BadRequestException("图片读取失败，请重新选择");
        }
    }

    private ImageFormat detect(byte[] bytes) {
        if (bytes.length >= 8
                && (bytes[0] & 0xff) == 0x89 && bytes[1] == 0x50 && bytes[2] == 0x4e && bytes[3] == 0x47) {
            return new ImageFormat("png", "image/png");
        }
        if (bytes.length >= 3
                && (bytes[0] & 0xff) == 0xff && (bytes[1] & 0xff) == 0xd8 && (bytes[2] & 0xff) == 0xff) {
            return new ImageFormat("jpg", "image/jpeg");
        }
        if (bytes.length >= 12
                && bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F'
                && bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P') {
            return new ImageFormat("webp", "image/webp");
        }
        throw new BadRequestException("不支持的图片格式");
    }

    private record ImageFormat(String extension, String contentType) {
    }
}
