package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.TypeModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeModuleRepository extends JpaRepository<TypeModuleEntity, Integer> {
}
