package com.resumestudio.storage;

import com.resumestudio.common.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final ImageStorageService storageService;

    @Value("${app.storage.max-image-bytes:8388608}")
    private long maxImageBytes;

    public StoredImage upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("请选择要上传的图片");
        }
        if (file.getSize() > maxImageBytes) {
            throw new BadRequestException("图片不能超过 " + (maxImageBytes / 1024 / 1024) + " MB");
        }

        try {
            byte[] bytes = file.getBytes();
            ImageFormat format = detect(bytes);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            if (image != null && (image.getWidth() > 8000 || image.getHeight() > 8000)) {
                throw new BadRequestException("图片尺寸过大，请压缩后上传");
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
        throw new BadRequestException("仅支持 JPG、PNG 或 WebP 图片");
    }

    private record ImageFormat(String extension, String contentType) {
    }
}
