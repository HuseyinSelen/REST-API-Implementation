package com.repsy.packagemanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repsy.packagemanager.entity.DependencyEntity;
import com.repsy.packagemanager.entity.PackageEntity;
import com.repsy.packagemanager.model.PackageMetadata;
import com.repsy.packagemanager.repository.DependencyRepository;
import com.repsy.packagemanager.repository.PackageRepository;
import com.repsy.storage.StorageService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("/")
@Validated
public class DeploymentController {

    private final PackageRepository packageRepository;
    private final DependencyRepository dependencyRepository;
    private final StorageService storageService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public DeploymentController(PackageRepository packageRepository,
                                DependencyRepository dependencyRepository,
                                StorageService storageService,
                                Validator validator) {
        this.packageRepository = packageRepository;
        this.dependencyRepository = dependencyRepository;
        this.storageService = storageService;
        this.objectMapper = new ObjectMapper();
        this.validator = validator;
    }

    @PostMapping("/{packageName}/{version}")
    public ResponseEntity<String> uploadPackage(
            @PathVariable String packageName,
            @PathVariable String version,
            @RequestPart("meta") MultipartFile metaFile,
            @RequestPart("file") MultipartFile repFile
    ) {
        try {
            // JSON'dan nesneye dönüştür
            PackageMetadata metadata = objectMapper.readValue(metaFile.getBytes(), PackageMetadata.class);

            // Elle validasyon uygula
            Set<ConstraintViolation<PackageMetadata>> violations = validator.validate(metadata);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException("Metadata doğrulaması başarısız", violations);
            }

            // Path ve metadata eşleşmesini kontrol et
            if (!packageName.equals(metadata.getName()) || !version.equals(metadata.getVersion())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Metadata ile path bilgisi eşleşmiyor.");
            }

            // Eski kayıt varsa sil
            packageRepository.findByNameAndVersion(packageName, version)
                    .ifPresent(packageRepository::delete);

            // Yeni kayıt ekle
            PackageEntity entity = new PackageEntity(metadata.getName(), metadata.getVersion(), metadata.getAuthor());
            packageRepository.save(entity);

            // Bağımlılıkları ekle
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

            // Dosyaları sakla
            storageService.store(packageName, version, metaFile, repFile);

            return ResponseEntity.ok("Paket başarıyla yüklendi.");

        } catch (ConstraintViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Validasyon hatası: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Yükleme başarısız: " + e.getMessage());
        }
    }
}
