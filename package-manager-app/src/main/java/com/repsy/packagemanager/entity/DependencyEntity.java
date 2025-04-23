package com.repsy.packagemanager.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dependencies")
public class DependencyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String packageName;
    private String version;

    @ManyToOne
    @JoinColumn(name = "package_id")
    private PackageEntity parentPackage;

    // Default constructor
    public DependencyEntity() {}

    // Custom constructor (eklenmesi gereken kısım)
    public DependencyEntity(String packageName, String version, PackageEntity parentPackage) {
        this.packageName = packageName;
        this.version = version;
        this.parentPackage = parentPackage;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public PackageEntity getParentPackage() {
        return parentPackage;
    }

    public void setParentPackage(PackageEntity parentPackage) {
        this.parentPackage = parentPackage;
    }
}
