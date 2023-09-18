package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.ModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends JpaRepository<ModuleEntity, Integer> {
}
