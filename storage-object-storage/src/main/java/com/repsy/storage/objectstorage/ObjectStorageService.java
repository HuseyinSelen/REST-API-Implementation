package com.repsy.storage.objectstorage;

import com.repsy.storage.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class ObjectStorageService implements StorageService {

    @Override
    public void store(String packageName, String version, MultipartFile meta, MultipartFile file) throws IOException {
        System.out.println("Simulated storing to Object Storage: " + packageName + " - " + version);
    }

    @Override
    public byte[] retrieve(String packageName, String version, String type) throws IOException {
        System.out.println("Simulated retrieving from Object Storage: " + packageName + " - " + version + " - " + type);
        return new byte[0]; // Simülasyon verisi
    }

    @Override
    public File load(String packageName, String version, String type) throws IOException {
        throw new UnsupportedOperationException("ObjectStorageService does not support load operation yet.");
    }
}
