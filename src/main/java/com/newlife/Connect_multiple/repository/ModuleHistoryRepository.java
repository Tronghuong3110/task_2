package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.ModuleHistoryEntity;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleHistoryRepository extends JpaRepository<ModuleHistoryEntity, String> {
    @Query(value = "SELECT id_probe_module, COUNT(status) as epw FROM module_history WHERE id_probe_module = :idProbeModule " +
            "AND at_time between :timeBefore AND :timeAfter AND status = :status group by (id_probe_module)", nativeQuery = true)
    JSONObject solveErrorPerWeekOfModule(@Param("idProbeModule") Integer idProbeModule,
                                         @Param("timeBefore") String timeBefore,
                                         @Param("timeAfter") String timeAfter,
                                         @Param("status") String status);
    @Query(value = "SELECT * FROM module_history WHERE id_probe = :idProbe", nativeQuery =  true)
    List<ModuleHistoryEntity> getAllByProbe(@Param("idProbe") Integer idProbe);

    @Query(value = "SELECT * FROM module_history WHERE id_probe_module = :idProbeModule", nativeQuery = true)
    List<ModuleHistoryEntity> getAllByModule(@Param("idProbeModule") Integer idProbeModule);

    @Query(value = "SELECT * FROM module_history WHERE at_time = :date", nativeQuery = true)
    List<ModuleHistoryEntity> getAllByDate(@Param("date") Date atTime);

    @Query(value = "SELECT * FROM module_history WHERE title LIKE %:str%", nativeQuery = true)
    List<ModuleHistoryEntity> searchAllByTitle(@Param("str") String str);

    @Query(value = "SELECT count(status) from module_history where id_probe_module = :idProbeModule Group by status", nativeQuery = true)
    Integer solveErrorPerWeek(@Param("idProbeModule") Integer idProbeModule);

    // Tên module / tên probe / thời gian / đã được ACK hay chưa
    @Query(value = "select * from module_history where (:idProbeModule is null or id_probe_module = :idProbeModule) " +
                    "and (:idProbe is null or id_probe = :idProbe) and ((at_time between :timeStart and :timeEnd ) " +
                    "or (:timeStart is null and :timeEnd is null)) " +
                    "and (:ack is null or ack = :ack ) and (:content is null or content like %:content%) and status = 'Failed' ", nativeQuery = true)
    Page<ModuleHistoryEntity> findAllByCondition(@Param("idProbeModule") Integer idProbeModule,
                                                 @Param("idProbe") Integer idProbe,
                                                 @Param("timeStart") String timeStart,
                                                 @Param("timeEnd") String timeEnd,
                                                 @Param("content") String content,
                                                 @Param("ack") Integer ack,
                                                 Pageable pageable);

    @Query(value = "select count(*) from module_history where (:idProbeModule is null or id_probe_module = :idProbeModule) " +
                    "and (:idProbe is null or id_probe = :idProbe) and ((at_time between :timeStart and :timeEnd ) or (:timeStart is null and :timeEnd is null)) " +
                    "and (:ack is null or ack = :ack ) and (:content is null or content like %:content%) and status = 'Failed' ", nativeQuery = true)
    Long count(@Param("idProbeModule") Integer idProbeModule,
                            @Param("idProbe") Integer idProbe,
                            @Param("timeStart") String timeStart,
                            @Param("timeEnd") String timeEnd,
                            @Param("content") String content,
                            @Param("ack") Integer ack);
}
