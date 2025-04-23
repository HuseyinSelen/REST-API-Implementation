package com.repsy.packagemanager.repository;

import com.repsy.packagemanager.entity.DependencyEntity;
import com.repsy.packagemanager.entity.PackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DependencyRepository extends JpaRepository<DependencyEntity, Long> {
    List<DependencyEntity> findByParentPackage(PackageEntity parentPackage);
}
