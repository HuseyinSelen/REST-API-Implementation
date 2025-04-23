package com.repsy.packagemanager.controller;

import com.repsy.packagemanager.dto.PackageResponseDTO;
import com.repsy.packagemanager.dto.PackageResponseDTO.DependencyDTO;
import com.repsy.packagemanager.entity.PackageEntity;
import com.repsy.packagemanager.entity.DependencyEntity;
import com.repsy.packagemanager.repository.PackageRepository;
import com.repsy.packagemanager.repository.DependencyRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/packages")
public class PackageController {

    private final PackageRepository packageRepository;
    private final DependencyRepository dependencyRepository;

    public PackageController(PackageRepository packageRepository, DependencyRepository dependencyRepository) {
        this.packageRepository = packageRepository;
        this.dependencyRepository = dependencyRepository;
    }

    @GetMapping("/{packageName}/{version}")
    public PackageResponseDTO getPackage(
            @PathVariable String packageName,
            @PathVariable String version
    ) {
        PackageEntity pkg = packageRepository.findByNameAndVersion(packageName, version)
                .orElseThrow(() -> new RuntimeException("Paket bulunamadı"));

        List<DependencyEntity> deps = dependencyRepository.findByParentPackage(pkg);

        List<DependencyDTO> dependencyDTOs = deps.stream()
                .map(dep -> new DependencyDTO(dep.getPackageName(), dep.getVersion()))
                .collect(Collectors.toList());

        return new PackageResponseDTO(
                pkg.getName(),
                pkg.getVersion(),
                pkg.getAuthor(),
                pkg.getUploadedAt(),
                dependencyDTOs
        );
    }
}
