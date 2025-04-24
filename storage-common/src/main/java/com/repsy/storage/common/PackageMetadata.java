package com.repsy.storage.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class PackageMetadata {

    @NotBlank(message = "Package name cannot be blank")
    private String name;

    @NotBlank(message = "Version cannot be blank")
    private String version;

    @NotBlank(message = "Author cannot be blank")
    private String author;

    @NotNull(message = "Dependencies cannot be null")
    @Valid
    private List<Dependency> dependencies;

    public static class Dependency {

        @JsonProperty("package")
        @NotBlank(message = "Dependency package name cannot be blank")
        private String packageName;

        @NotBlank(message = "Dependency version cannot be blank")
        private String version;

        public Dependency() {}

        public Dependency(String packageName, String version) {
            this.packageName = packageName;
            this.version = version;
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
    }

    public PackageMetadata() {}

    public PackageMetadata(String name, String version, String author, List<Dependency> dependencies) {
        this.name = name;
        this.version = version;
        this.author = author;
        this.dependencies = dependencies;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }
}
