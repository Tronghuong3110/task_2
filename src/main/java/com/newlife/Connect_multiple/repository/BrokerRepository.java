package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.BrokerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrokerRepository extends JpaRepository<BrokerEntity, Integer> {
}
