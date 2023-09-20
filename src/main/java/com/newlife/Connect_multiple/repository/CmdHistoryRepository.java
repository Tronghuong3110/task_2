package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.CmdHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CmdHistoryRepository extends JpaRepository<CmdHistoryEntity, String> {
}
