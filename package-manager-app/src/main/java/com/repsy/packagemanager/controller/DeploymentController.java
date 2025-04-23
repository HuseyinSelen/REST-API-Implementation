package com.repsy.packagemanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repsy.packagemanager.entity.DependencyEntity;
import com.repsy.packagemanager.entity.PackageEntity;
import com.repsy.packagemanager.model.PackageMetadata;
import com.repsy.packagemanager.repository.DependencyRepository;
import com.repsy.packagemanager.repository.PackageRepository;
import com.repsy.storage.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/")
public class DeploymentController {

    private final PackageRepository packageRepository;
    private final DependencyRepository dependencyRepository;
    private final StorageService storageService;
    private final ObjectMapper objectMapper;

    public DeploymentController(PackageRepository packageRepository,
                                DependencyRepository dependencyRepository,
                                StorageService storageService) {
        this.packageRepository = packageRepository;
        this.dependencyRepository = dependencyRepository;
        this.storageService = storageService;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping("/{packageName}/{version}")
    public ResponseEntity<String> uploadPackage(
            @PathVariable String packageName,
            @PathVariable String version,
            @RequestPart("meta") MultipartFile metaFile,
            @RequestPart("file") MultipartFile repFile
    ) {
        try {
            PackageMetadata metadata = objectMapper.readValue(metaFile.getBytes(), PackageMetadata.class);
    
            if (!packageName.equals(metadata.getName()) || !version.equals(metadata.getVersion())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Metadata ile path bilgisi eşleşmiyor.");
            }
    
            // Eğer varsa önceki veriyi sil
            packageRepository.findByNameAndVersion(packageName, version)
                    .ifPresent(packageRepository::delete);
    
            // Package kaydı oluştur
            PackageEntity entity = new PackageEntity(metadata.getName(), metadata.getVersion(), metadata.getAuthor());
            packageRepository.save(entity);
    
            // Dependency'leri kaydet
            if (metadata.getDependencies() != null) {
                for (PackageMetadata.Dependency dep : metadata.getDependencies()) {
                    DependencyEntity dependency = new DependencyEntity(
                            dep.getPackageName(),
                            dep.getVersion(),
                            entity
                    );
                    dependencyRepository.save(dependency);
                }
            }
    
            // Dosyayı kaydet
            storageService.store(packageName, version, metaFile, repFile);
    
            return ResponseEntity.ok("Paket başarıyla yüklendi.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Yükleme başarısız: " + e.getMessage());
        }
    }
    
}
