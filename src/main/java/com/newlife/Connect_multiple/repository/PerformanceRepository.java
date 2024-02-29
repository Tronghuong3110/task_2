package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.PerformanceCpu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<PerformanceCpu, Integer> {
    @Query(value = "select * from cpu where cpu.time like CONCAT(:modifiedTime, '%') and id_probe = :probeId order by time DESC", nativeQuery = true)
    List<PerformanceCpu> findAllByModifiedTimeAndProbeId(@Param("modifiedTime") String modifiedTime,
                                                         @Param("probeId") Integer probeId);

    @Query(value = "select * from cpu where time between :time_before and :time_after and id_probe = :probeId order by time DESC limit :number ;", nativeQuery = true)
    List<PerformanceCpu> findAllByModifiedTime(@Param("time_before") LocalDateTime timeBefore,
                                               @Param("time_after") LocalDateTime timeAfter,
                                               @Param("probeId") Integer probeId,
                                               @Param("number") Integer number);

    @Modifying
    @Transactional
    @Query(value = "delete from cpu where time <= :currentTime ", nativeQuery = true)
    void deletecpu(@Param("currentTime") LocalDateTime currentTime);

    void deleteAllByTimeBefore(String currentTime);
}
