package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.ProbeHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProbeHistoryRepository extends JpaRepository<ProbeHistoryEntity, Integer> {
    @Query(value = "select * from module_history order by id_probe_history desc  limit num", nativeQuery = true)
    List<ProbeHistoryEntity> findLastNRecord(@Param("num") Integer n);
}
