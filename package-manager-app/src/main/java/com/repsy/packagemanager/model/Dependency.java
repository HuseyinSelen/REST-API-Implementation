package com.repsy.packagemanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class Dependency {

    @JsonProperty("package")
    @NotBlank(message = "Dependency package name cannot be blank")
    private String packageName;

    @NotBlank(message = "Dependency version cannot be blank")
    private String version;

    // Getters & Setters
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
