package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.ProbeModuleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleProbeRepository extends JpaRepository<ProbeModuleEntity, Integer> {

    @Query(value = "select * from probe_module where module_name COLLATE Latin1_General_CI_AI like %:name% " +
                    "and status like %:status%", nativeQuery = true)
    Page<ProbeModuleEntity> findAllByProbeNameOrStatus(@Param("name") String name,
                                                       @Param("status") String status,
                                                       Pageable pageable);

    @Query(value = "select * from probemodule.probe_module where id_probe = :probeId and (status = :status1 or status = :status2", nativeQuery = true)
    List<ProbeModuleEntity> findAllModuleByProbeIdAndStatus(@Param("probeId") Integer id,
                                                            @Param("status1") String status1,
                                                            @Param("status2") String status2);
}
