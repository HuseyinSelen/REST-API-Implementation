package com.repsy.storage.filesystem;

import com.repsy.storage.StorageService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component("inMemoryStorageService")
public class InMemoryStorageService implements StorageService {

    private final Map<String, byte[]> storage = new HashMap<>();

    @Override
    public void store(String packageName, String version, MultipartFile metaFile, MultipartFile repFile) {
        try {
            storage.put(packageKey(packageName, version, "meta.json"), metaFile.getBytes());
            storage.put(packageKey(packageName, version, "package.rep"), repFile.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to store files in memory", e);
        }
    }

    @Override
    public File load(String packageName, String version, String fileName) {
        String key = packageKey(packageName, version, fileName);
        byte[] data = storage.get(key);
        if (data == null) {
            throw new RuntimeException("File not found in memory: " + key);
        }

        try {
            File tempFile = File.createTempFile("download-", "-" + fileName);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(data);
            }
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file from memory", e);
        }
    }

    @Override
    public byte[] retrieve(String packageName, String version, String type) throws IOException {
            throw new UnsupportedOperationException("InMemoryStorageService does not support retrieve.");
    }


    private String packageKey(String packageName, String version, String fileName) {
        return packageName + "/" + version + "/" + fileName;
    }
}
