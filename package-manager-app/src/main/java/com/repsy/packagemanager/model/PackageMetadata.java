package com.repsy.packagemanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PackageMetadata {

    private String name;
    private String version;
    private String author;
    private List<Dependency> dependencies;

    // Inner class for dependencies
    public static class Dependency {

        @JsonProperty("package")
        private String packageName;

        private String version;

        // Constructors
        public Dependency() {}

        public Dependency(String packageName, String version) {
            this.packageName = packageName;
            this.version = version;
        }

        // Getters and setters
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

    // Constructors
    public PackageMetadata() {}

    public PackageMetadata(String name, String version, String author, List<Dependency> dependencies) {
        this.name = name;
        this.version = version;
        this.author = author;
        this.dependencies = dependencies;
    }

    // Getters and setters
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
