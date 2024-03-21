package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.DatabaseServerMysql;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DatabaseServerRepository extends JpaRepository<DatabaseServerMysql, Integer> {

    @Query(value = "select * from database_server where (ip_server like %:key% or server_name like %:key%) and active = 1", nativeQuery = true)
    List<DatabaseServerMysql> findAllDatabaseServerByKey(@Param("key") String key);

    Boolean existsByPortNumber(Integer portNumber);
    Boolean existsByServerName(String serverName);
    Boolean existsByIpServer(String ipServer);

    Optional<DatabaseServerMysql> findByIpServer(String ipServer);

    @Query(value = "select * from database_server where ip_server like %:ip% and type like %:typeStr% and active = 1", nativeQuery = true)
    List<DatabaseServerMysql> findByIpOrType(@Param("ip") String ip,
                                             @Param("typeStr") String type);
}
