package com.repsy.packagemanager.config;

import com.repsy.storage.StorageService;
import com.repsy.storage.filesystem.FileSystemStorageService;
import com.repsy.storage.objectstorage.ObjectStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageStrategyConfig {

    @Value("${storage.strategy:file-system}")
    private String strategy;

    private final FileSystemStorageService fileSystemStorageService;
    private final ObjectStorageService objectStorageService;

    public StorageStrategyConfig(
            FileSystemStorageService fileSystemStorageService,
            ObjectStorageService objectStorageService
    ) {
        this.fileSystemStorageService = fileSystemStorageService;
        this.objectStorageService = objectStorageService;
    }

    @Bean
    public StorageService storageService() {
        return switch (strategy.toLowerCase()) {
            case "file-system" -> fileSystemStorageService;
            case "object-storage" -> objectStorageService;
            default -> throw new IllegalArgumentException("Desteklenmeyen storage strategy: " + strategy);
        };
    }
}
