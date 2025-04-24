package com.repsy.packagemanager.controller;

import com.repsy.storage.StorageService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/api/packages")
public class DownloadController {

    private final StorageService storageService;

    public DownloadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/{packageName}/{version}/{fileName:.+}")
public ResponseEntity<Resource> downloadFile(
        @PathVariable String packageName,
        @PathVariable String version,
        @PathVariable String fileName
) {
    try {
        if (fileName == null || fileName.contains("..") || fileName.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        File file = storageService.load(packageName, version, fileName);
        if (file == null || !file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).build();
    }
}

}
