package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.ProbeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProbeRepository extends JpaRepository<ProbeEntity, Integer> {
    Boolean existsByIpAddress(String ipAddress);
    Boolean existsByName(String probeName);

    @Query(value = "select * from probe where (name COLLATE Latin1_General_CI_AI like %:name%) " +
                    "and (location COLLATE Latin1_General_CI_AI like %:location%) " +
                    "and (area COLLATE Latin1_General_CI_AI like %:area%) " +
                    "and (vlan like %:vlan%) " +
                    "and deleted = 0", nativeQuery = true)
    Page<ProbeEntity> findByNameOrLocationOrAreaOrVlan(@Param("name") String name,
                                                       @Param("location") String location,
                                                       @Param("area") String area,
                                                       @Param("vlan") String vlan,
                                                       Pageable pageable);
}






