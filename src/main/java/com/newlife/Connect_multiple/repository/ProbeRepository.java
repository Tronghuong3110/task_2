package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.ProbeEntity;
import org.json.simple.JSONObject;
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
    Boolean existsByIpAddressAndDeleted(String ipAddress, Integer deleted);
    Boolean existsByNameAndDeleted(String probeName, Integer deleted);
    @Query(value = "select * from probe where (name  like %:name%) " + // COLLATE Latin1_General_CI_AI
                    "and (location like %:location%) " +
                    "and (area like %:area%) " +
                    "and (vlan like %:vlan%) " +
                    "and deleted = :deleted", nativeQuery = true)
    List<ProbeEntity> findByNameOrLocationOrAreaOrVlan(@Param("name") String name,
                                                       @Param("location") String location,
                                                       @Param("area") String area,
                                                       @Param("vlan") String vlan,
                                                       @Param("deleted") Integer deleted);

//    @Query(value = "select * from probemodule.probe where deleted = :deleted ", nativeQuery = true)
    List<ProbeEntity> findProbeByDeletedAndStatus(Integer deleted, String status);
    Integer countAllByStatusAndDeleted(String status, Integer deleted);
    Optional<ProbeEntity> findByName(String name);
    Optional<ProbeEntity> findByIpAddress(String ipAddress);
    Optional<ProbeEntity> findByIdAndDeleted(Integer id, Integer deleted);
    Optional<ProbeEntity> findByIdAndStatus(Integer id, String status);

    @Query(value = "SELECT id_probe, JSON_OBJECTAGG(status, status_count) AS status_counts " +
            "FROM(SELECT id_probe, status, COUNT(status) AS status_count FROM probemodule.probe_module " +
            "GROUP BY id_probe, status) subquery " +
            "GROUP BY id_probe;", nativeQuery = true)
    List<JSONObject> countStatusByProbe();

    @Query(value = "select count(*) from probe where (name  like %:name%) " + // COLLATE Latin1_General_CI_AI
            "and deleted = :deleted", nativeQuery = true)
    Long countAllByDeleted(@Param("deleted") Integer deleted,
                           @Param("name") String name);

    @Query(value = "select * from probe where (name like %:name%) " + // COLLATE Latin1_General_CI_AI
            "and deleted = :deleted", nativeQuery = true)
    List<ProbeEntity> findByName(@Param("name") String name,
                                                       @Param("deleted") Integer deleted,
                                                       Pageable pageable);
}






