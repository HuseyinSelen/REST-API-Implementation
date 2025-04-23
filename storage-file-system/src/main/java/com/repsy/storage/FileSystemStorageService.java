package com.repsy.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service("fileSystemStorageService")
public class FileSystemStorageService implements StorageService {

    @Value("${storage.path:storage}")
    private String storagePath;

    @Override
    public void store(String packageName, String version, MultipartFile meta, MultipartFile rep) throws IOException {
        String path = System.getProperty("user.dir") + File.separator + storagePath +
                      File.separator + packageName + File.separator + version;

        File destFolder = new File(path);
        if (!destFolder.exists()) {
            boolean created = destFolder.mkdirs();
            if (!created) {
                throw new IOException("Klasör oluşturulamadı: " + path);
            }
        }

        meta.transferTo(new File(destFolder, "meta.json"));
        rep.transferTo(new File(destFolder, "package.rep"));
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
