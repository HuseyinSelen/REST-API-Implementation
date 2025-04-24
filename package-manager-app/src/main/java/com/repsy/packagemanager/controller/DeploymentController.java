package com.repsy.packagemanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repsy.packagemanager.entity.DependencyEntity;
import com.repsy.packagemanager.entity.PackageEntity;
import com.repsy.storage.common.model.PackageMetadata;
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
            if (metaFile.isEmpty() || repFile.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("meta veya package dosyası eksik.");
            }

            // JSON'dan nesneye dönüştür
            PackageMetadata metadata = objectMapper.readValue(metaFile.getBytes(), PackageMetadata.class);

            // Elle validasyon uygula
            Set<ConstraintViolation<PackageMetadata>> violations = validator.validate(metadata);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException("Metadata doğrulaması başarısız", violations);
            }

            // Path ve metadata eşleşmesini kontrol et (trim + case-insensitive)
            String pathName = packageName.trim();
            String pathVersion = version.trim();
            String metaName = metadata.getName().trim();
            String metaVersion = metadata.getVersion().trim();

System.out.println("== Karşılaştırma Öncesi ==");
System.out.println("Path Name     : [" + pathName + "]");
System.out.println("Meta Name     : [" + metaName + "]");
System.out.println("Path Version  : [" + pathVersion + "]");
System.out.println("Meta Version  : [" + metaVersion + "]");
System.out.println("İsim eşleşiyor?: " + pathName.equalsIgnoreCase(metaName));
System.out.println("Versiyon eşleşiyor?: " + pathVersion.equalsIgnoreCase(metaVersion));

            if (!pathName.equalsIgnoreCase(metaName) || !pathVersion.equalsIgnoreCase(metaVersion)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(String.format("Metadata ile path bilgisi eşleşmiyor. Path: %s/%s, Meta: %s/%s",
                                pathName, pathVersion, metaName, metaVersion));
            }

            // Eski kayıt varsa sil
            packageRepository.findByNameAndVersion(pathName, pathVersion)
                    .ifPresent(packageRepository::delete);

            // Yeni kayıt ekle
            PackageEntity entity = new PackageEntity(metaName, metaVersion, metadata.getAuthor());
            packageRepository.save(entity);

            // Bağımlılıkları ekle
            if (metadata.getDependencies() != null) {
                for (PackageMetadata.Dependency dep : metadata.getDependencies()) {
                    DependencyEntity dependency = new DependencyEntity(
                            dep.getPackageName().trim(),
                            dep.getVersion().trim(),
                            entity
                    );
                    dependencyRepository.save(dependency);
                }
            }

            // Dosyaları sakla
            storageService.store(pathName, pathVersion, metaFile, repFile);

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
