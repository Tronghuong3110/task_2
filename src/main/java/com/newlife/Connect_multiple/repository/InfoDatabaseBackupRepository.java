package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.mysql.InfoDatabaseBackUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InfoDatabaseBackupRepository extends JpaRepository<InfoDatabaseBackUp, Integer> {

    @Query(value = "select * from info_database_backup where name_database_ftp_server like :databaseName% and (id = :id_info or :id_info is null)", nativeQuery = true)
    List<InfoDatabaseBackUp> findAllGroupById(@Param("databaseName") String databaseName,
                                              @Param("id_info")Integer id_info);
}
