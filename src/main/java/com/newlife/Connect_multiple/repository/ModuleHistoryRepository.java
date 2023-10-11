package com.newlife.Connect_multiple.repository;

import com.newlife.Connect_multiple.entity.ModuleHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleHistoryRepository extends JpaRepository<ModuleHistoryEntity, String> {
    @Query(value = "SELECT COUNT(status) as epw FROM module_history WHERE (id_probe_module = :idProbeModule) " +
            "(AND at_time >= :timeBefore AND at_time <= :timeAfter) " + " (AND status = :status)", nativeQuery = true)
    Optional<Integer> solveErrorPerWeekOfModule(@Param("idProbeModule") Integer idProbeModule, @Param("timeBefore") Date timeBefore,
                                       @Param("timeAfter") Date timeAfter, @Param("status") String status);
    @Query(value = "SELECT * FROM module_history WHERE id_probe = :idProbe", nativeQuery =  true)
    List<ModuleHistoryEntity> getAllByProbe(@Param("idProbe") Integer idProbe);

    @Query(value = "SELECT * FROM module_history WHERE id_probe_module = :idProbeModule", nativeQuery = true)
    List<ModuleHistoryEntity> getAllByModule(@Param("idProbeModule") Integer idProbeModule);

    @Query(value = "SELECT * FROM module_history WHERE at_time = :date", nativeQuery = true)
    List<ModuleHistoryEntity> getAllByDate(@Param("date") Date atTime);

    @Query(value = "SELECT * FROM module_history WHERE title LIKE %:str%", nativeQuery = true)
    List<ModuleHistoryEntity> searchAllByTitle(@Param("str") String str);

//    @Query(value = "SELECT COUNT(status) FROM module_history WHERE (id_probe_module = :idProbeModule) " +
//            "(AND at_time >= :timeBefore AND at_time <= :timeAfter) " + " (AND status = :status)", nativeQuery = true)
//    Integer solveErrorPerWeekOfModule(@Param("idProbeModule") Integer idProbeModule, @Param("timeBefore") Date timeBefore,
//                                      @Param("timeAfter") Date timeAfter, @Param("status") String status);
    @Query(value = "SELECT count(status) from module_history where id_probe_module = :idProbeModule Group by status", nativeQuery = true)
    Integer solveErrorPerWeek(@Param("idProbeModule") Integer idProbeModule);
}
