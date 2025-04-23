package com.repsy.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface StorageService {
    void store(String packageName, String version, MultipartFile meta, MultipartFile rep) throws IOException;
    File load(String packageName, String version, String fileName) throws IOException;
}
