package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.ProbeHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProbeHistoryRepository extends JpaRepository<ProbeHistoryEntity, Integer> {
}
