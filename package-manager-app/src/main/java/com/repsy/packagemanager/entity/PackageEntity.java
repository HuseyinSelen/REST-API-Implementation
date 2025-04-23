package com.repsy.packagemanager.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "packages")
public class PackageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String version;
    private String author;

    private LocalDateTime uploadedAt;

    @OneToMany(mappedBy = "parentPackage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DependencyEntity> dependencies = new ArrayList<>();

    // Constructors
    public PackageEntity() {
        this.uploadedAt = LocalDateTime.now();
    }

    public PackageEntity(String name, String version, String author) {
        this.name = name;
        this.version = version;
        this.author = author;
        this.uploadedAt = LocalDateTime.now();
    }

    // Getters & Setters

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getVersion() { return version; }

    public void setVersion(String version) { this.version = version; }

    public String getAuthor() { return author; }

    public void setAuthor(String author) { this.author = author; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }

    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public List<DependencyEntity> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<DependencyEntity> dependencies) {
        this.dependencies = dependencies;
    }
}
