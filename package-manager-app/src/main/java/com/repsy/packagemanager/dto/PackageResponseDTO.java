package com.repsy.packagemanager.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PackageResponseDTO {
    private String name;
    private String version;
    private String author;
    private LocalDateTime uploadedAt;
    private List<DependencyDTO> dependencies;

    public static class DependencyDTO {
        private String packageName;
        private String version;

        public DependencyDTO(String packageName, String version) {
            this.packageName = packageName;
            this.version = version;
        }

        public String getPackageName() { return packageName; }
        public String getVersion() { return version; }
    }

    // Constructor, Getters & Setters

    public PackageResponseDTO(String name, String version, String author, LocalDateTime uploadedAt, List<DependencyDTO> dependencies) {
        this.name = name;
        this.version = version;
        this.author = author;
        this.uploadedAt = uploadedAt;
        this.dependencies = dependencies;
    }

    public String getName() { return name; }
    public String getVersion() { return version; }
    public String getAuthor() { return author; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public List<DependencyDTO> getDependencies() { return dependencies; }
}
