package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.MemoryClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemoryRepository extends JpaRepository<MemoryClient, Integer> {
    @Query(value = "select * from memory limit 10", nativeQuery = true)
    List<MemoryClient> findAllByProbeId(Integer probeId);

    @Query(value = "select * from memory where modified_time between :start and :end", nativeQuery = true)
    Optional<MemoryClient> findByTime(@Param("start") String start,
                                      @Param("end") String end);
}
