package com.repsy.storage.objectstorage;

import com.repsy.storage.StorageService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class ObjectStorageService implements StorageService {

    private final MinioClient minioClient;
    private final String bucketName;

    public ObjectStorageService(
            @Value("${minio.url}") String url,
            @Value("${minio.accessKey}") String accessKey,
            @Value("${minio.secretKey}") String secretKey,
            @Value("${minio.bucket}") String bucketName
    ) {
        this.bucketName = bucketName;
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Override
    public void store(String packageName, String version, MultipartFile meta, MultipartFile file) throws IOException {
        try {
            String basePath = packageName + "/" + version + "/";

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(basePath + "meta.json")
                    .stream(meta.getInputStream(), meta.getSize(), -1)
                    .contentType(meta.getContentType())
                    .build());

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(basePath + file.getOriginalFilename())
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new IOException("MinIO store error: " + e.getMessage(), e);
        }
    }

    @Override
public File load(String packageName, String version, String fileName) throws IOException {
    try {
        System.out.println("▶ load() çağrıldı: fileName = " + fileName);

        // path traversal kontrolü yap, ama sadece bu kadar
        if (fileName == null || fileName.contains("..") || fileName.isBlank()) {
            throw new IllegalArgumentException("Geçersiz dosya adı: " + fileName);
        }

        String objectName = packageName + "/" + version + "/" + fileName;
        File tempFile = File.createTempFile("minio-", "-" + fileName);

        try (InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
             FileOutputStream out = new FileOutputStream(tempFile)) {

            stream.transferTo(out);
        }

        return tempFile;

    } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
        throw new IOException("MinIO load error: " + e.getMessage(), e);
    }
}



    @Override
    public byte[] retrieve(String packageName, String version, String type) throws IOException {
        return new byte[0]; // kullanılmıyorsa boş bırakılabilir
    }
}
