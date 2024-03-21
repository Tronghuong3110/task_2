package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.dto.InfoCaptureSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IInfocaptureRepository extends JpaRepository<InfoCaptureSetting, Integer> {
//    @Query(value = "delete from info_capture_setting where id_server=:idServer", nativeQuery = true)
    void deleteAllByIdServer(Integer idServer);
}
