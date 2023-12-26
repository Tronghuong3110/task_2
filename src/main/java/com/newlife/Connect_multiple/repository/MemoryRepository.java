package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.MemoryClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
//@EnableJpaRepositories
public interface MemoryRepository extends JpaRepository<MemoryClient, Integer> {
    @Query(value = "select * from memory limit 10", nativeQuery = true)
    List<MemoryClient> findAllByProbeId(Integer probeId);

    @Query(value = "select * from memory where modified_time between :start and :end", nativeQuery = true)
    Optional<MemoryClient> findByTime(@Param("start") String start,
                                      @Param("end") String end);
    Optional<MemoryClient> findByDiskNameAndProbeId(String diskName, Integer probeId);

    @Query(value = "SELECT JSON_OBJECT('memories', JSON_ARRAYAGG(JSON_OBJECT('disk_name', disk_name, 'memory_free', memory_disk, 'total_memory', total_memory, 'percent', memory_disk/total_memory))) as memories " +
            "FROM memory where probe_id = :probeId " +
            "GROUP BY probe_id", nativeQuery = true)
    JSONArray findAllMemory(@Param("probeId") Integer probeId);

}
