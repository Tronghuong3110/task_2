package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.ProbeModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleProbeRepository extends JpaRepository<ProbeModuleEntity, Integer> {
}
