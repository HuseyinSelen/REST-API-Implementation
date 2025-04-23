package com.repsy.packagemanager.config;

import com.repsy.storage.FileSystemStorageService;
import com.repsy.storage.InMemoryStorageService;
import com.repsy.storage.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageStrategyConfig {

    @Value("${storage.strategy:file-system}")
    private String strategy;

    private final FileSystemStorageService fileSystemStorageService;
    private final InMemoryStorageService inMemoryStorageService;

    public StorageStrategyConfig(
            FileSystemStorageService fileSystemStorageService,
            InMemoryStorageService inMemoryStorageService
    ) {
        this.fileSystemStorageService = fileSystemStorageService;
        this.inMemoryStorageService = inMemoryStorageService;
    }

    @Bean
    public StorageService storageService() {
        return switch (strategy.toLowerCase()) {
            case "file-system" -> fileSystemStorageService;
            case "in-memory" -> inMemoryStorageService;
            default -> throw new IllegalArgumentException("Desteklenmeyen storage strategy: " + strategy);
        };
    }
}
