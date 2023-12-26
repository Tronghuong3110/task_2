package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.PerformanceCpu;
import org.json.simple.JSONArray;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<PerformanceCpu, Integer> {
    @Query(value = "select * from performance_cpu where performance_cpu.modified_time like CONCAT(:modifiedTime, '%') and probe_id = :probeId", nativeQuery = true)
    List<PerformanceCpu> findAllByModifiedTimeAndProbeId(@Param("modifiedTime") String modifiedTime,
                                                         @Param("probeId") Integer probeId);

    @Query(value = "select * from performance_cpu where modified_time between :time_before and :time_after and probe_id = :probeId limit 10 ;", nativeQuery = true)
    List<PerformanceCpu> findAllByModifiedTime(@Param("time_before") String timeBefore,
                                    @Param("time_after") String timeAfter,
                                    @Param("probeId") Integer probeId);
}
