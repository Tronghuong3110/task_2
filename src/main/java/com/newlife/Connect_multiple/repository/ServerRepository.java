package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.ServerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerRepository extends JpaRepository<ServerEntity, Integer> {
}
