package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.ProbeOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProbeOptionRepository extends JpaRepository<ProbeOptionEntity, Integer> {

    Boolean existsByUserName(String username);
}
