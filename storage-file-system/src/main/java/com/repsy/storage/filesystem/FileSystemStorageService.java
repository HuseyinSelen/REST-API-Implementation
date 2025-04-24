package com.repsy.storage.filesystem;

import com.repsy.storage.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileSystemStorageService implements StorageService {

    @Value("${storage.path:storage}")
    private String storagePath;

    @Override
    public void store(String packageName, String version, MultipartFile meta, MultipartFile rep) throws IOException {
        String path = System.getProperty("user.dir") + File.separator + storagePath +
                      File.separator + packageName + File.separator + version;

        File destFolder = new File(path);
        if (!destFolder.exists() && !destFolder.mkdirs()) {
            throw new IOException("Klasör oluşturulamadı: " + path);
        }

        meta.transferTo(new File(destFolder, "meta.json"));
        rep.transferTo(new File(destFolder, "package.rep"));
    }

    @Override
    public byte[] retrieve(String packageName, String version, String type) throws IOException {
        String fileName = switch (type.toLowerCase()) {
            case "meta" -> "meta.json";
            case "rep" -> "package.rep";
            default -> throw new IllegalArgumentException("Geçersiz dosya tipi: " + type);
        };

        String path = System.getProperty("user.dir") + File.separator + storagePath +
                      File.separator + packageName + File.separator + version + File.separator + fileName;

        Path filePath = new File(path).toPath();
        if (!Files.exists(filePath)) {
            throw new IOException("Dosya bulunamadı: " + path);
        }

        return Files.readAllBytes(filePath);
    }

    @Override
public File load(String packageName, String version, String fileName) throws IOException {
    String path = System.getProperty("user.dir") + File.separator + storagePath +
                  File.separator + packageName + File.separator + version + File.separator + fileName;

    File file = new File(path);
    if (!file.exists()) {
        throw new IOException("Dosya bulunamadı: " + path);
    }

    return file;
}

}
