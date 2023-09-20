package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.ModuleHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleHistoryRepository extends JpaRepository<ModuleHistoryEntity, String> {
}
