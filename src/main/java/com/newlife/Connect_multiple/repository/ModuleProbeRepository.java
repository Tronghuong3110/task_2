package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.ProbeModuleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleProbeRepository extends JpaRepository<ProbeModuleEntity, Integer> {

    @Query(value = "select * from probe_module where module_name COLLATE Latin1_General_CI_AI like %:name% " +
                    "and status like %:status%", nativeQuery = true)
    Page<ProbeModuleEntity> findAllByProbeNameOrStatus(@Param("name") String name,
                                                       @Param("status") String status,
                                                       Pageable pageable);
}
