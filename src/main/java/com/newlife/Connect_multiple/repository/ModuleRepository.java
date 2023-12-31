package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.ModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface ModuleRepository extends JpaRepository<ModuleEntity, Integer> {

    @Query(value = "select * from module where name Collate Latin1_General_CI_AI like %:name%", nativeQuery = true)
    List<ModuleEntity> findAllByName(@Param("name") String name);
}
