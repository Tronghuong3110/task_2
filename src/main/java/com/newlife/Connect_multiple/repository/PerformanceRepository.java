package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.PerformanceCpu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceRepository extends JpaRepository<PerformanceCpu, Integer> {
}
