package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.ProbeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProbeRepository extends JpaRepository<ProbeEntity, Integer> {
    Boolean existsByIpAddress(String ipAddress);
    Boolean existsByName(String probeName);
    @Query(value = "select * from probe where (name  like %:name%) " + // COLLATE Latin1_General_CI_AI
                    "and (location like %:location%) " +
                    "and (area like %:area%) " +
                    "and (vlan like %:vlan%) " +
                    "and deleted = 0", nativeQuery = true)
    List<ProbeEntity> findByNameOrLocationOrAreaOrVlan(@Param("name") String name,
                                                       @Param("location") String location,
                                                       @Param("area") String area,
                                                       @Param("vlan") String vlan);

    @Query(value = "select * from probemodule.probe where status = :status ", nativeQuery = true)
    List<ProbeEntity> findProbeByStatus(@Param("status") String status);
}






