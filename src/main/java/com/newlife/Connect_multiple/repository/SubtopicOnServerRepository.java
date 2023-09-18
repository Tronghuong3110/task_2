package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.SubtopicServerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubtopicOnServerRepository extends JpaRepository<SubtopicServerEntity, Integer> {
}
