package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.NetworkInterfaceEntity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface INetworkInterfaceRepository extends JpaRepository<NetworkInterfaceEntity, Integer> {
    Optional<NetworkInterfaceEntity> findByInterfaceNameAndIdProbeAndMonitor(String name, Integer idProbe, Integer monitor);
    void deleteAllByIdProbe(Integer idProbe);
    List<NetworkInterfaceEntity> findAllByIdProbe(Integer idProbe);
    @Query(value = "select status, count(*) AS cnt from network_interface where id_probe = :idProbe group by status", nativeQuery = true)
    List<Map<String, Object>> countAllByStatus(@Param("idProbe") Integer idProbe);
}
