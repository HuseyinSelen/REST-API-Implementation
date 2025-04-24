package com.repsy.storage.objectstorage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repsy.storage.StorageService;
import com.repsy.storage.common.model.PackageMetadata;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class ObjectStorageService implements StorageService {

    private final MinioClient minioClient;
    private final String bucketName;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        System.out.println("📦 [ObjectStorageService] aktif storage provider olarak yüklendi.");
        System.out.println("📦 [MinIO Bucket] -> " + bucketName);
    }

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
            if (meta.isEmpty()) {
                throw new IOException("meta.json içeriği boş olamaz.");
            }

            // JSON içeriğini kontrol et
            PackageMetadata metadata = objectMapper.readValue(meta.getInputStream(), PackageMetadata.class);

            if (metadata.getName().isBlank() ||
                metadata.getVersion().isBlank() ||
                metadata.getAuthor().isBlank()) {
                throw new IOException("meta.json içindeki alanlar boş olamaz.");
            }

            for (PackageMetadata.Dependency dep : metadata.getDependencies()) {
                if (dep.getPackageName().isBlank() || dep.getVersion().isBlank()) {
                    throw new IOException("Tüm dependency package ve version alanları dolu olmalıdır.");
                }
            }

            String basePath = packageName + "/" + version + "/";
            System.out.println("📤 Dosya yükleniyor: " + basePath + "package.rep");

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(basePath + "meta.json")
                    .stream(meta.getInputStream(), meta.getSize(), -1)
                    .contentType(meta.getContentType())
                    .build());

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(basePath + "package.rep")
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            System.out.println("✅ Dosyalar başarıyla yüklendi: " + basePath);

        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            System.err.println("❌ MinIO store hatası: " + e.getMessage());
            throw new IOException("MinIO store error: " + e.getMessage(), e);
        }
    }

    @Override
    public File load(String packageName, String version, String fileName) throws IOException {
        try {
            System.out.println("📂 [DEBUG] Gelen fileName: [" + fileName + "]");
            System.out.println("➡ contains(\"..\")? " + fileName.contains(".."));
            System.out.println("➡ isBlank()? " + fileName.isBlank());

            if (fileName == null || fileName.contains("..") || fileName.isBlank()) {
                System.err.println("❗ Geçersiz dosya adı tespit edildi: " + fileName);
                return null;
            }

            String objectName = packageName + "/" + version + "/" + fileName;
            System.out.println("📥 MinIO'dan dosya indiriliyor: " + objectName);

            File tempFile = File.createTempFile("minio-", "-" + fileName);

            try (InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
                 FileOutputStream out = new FileOutputStream(tempFile)) {

                stream.transferTo(out);
            }

            System.out.println("✅ Dosya indirildi: " + tempFile.getAbsolutePath());
            return tempFile;

        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            System.err.println("❌ MinIO load hatası: " + e.getMessage());
            throw new IOException("MinIO load error: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] retrieve(String packageName, String version, String type) throws IOException {
        return new byte[0];
    }
}
