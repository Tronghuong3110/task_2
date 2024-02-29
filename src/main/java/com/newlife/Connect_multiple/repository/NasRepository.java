package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.NasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NasRepository extends JpaRepository<NasEntity, Integer> {
    Boolean existsByIp(String ip);
    Optional<NasEntity> findByIp(String ip);
}
