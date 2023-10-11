package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.dto.ModuleHistory;
import com.newlife.Connect_multiple.entity.ProbeModuleEntity;
import com.newlife.Connect_multiple.repository.ModuleHistoryRepository;
import com.newlife.Connect_multiple.repository.ModuleProbeRepository;
import com.newlife.Connect_multiple.service.IModuleHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ModuleHistoryService implements IModuleHistoryService {

    @Autowired
    private static ModuleHistoryRepository moduleHistoryRepository;

    @Autowired
    private static ModuleProbeRepository moduleProbeRepository;


    // HAN
    @Override
    public String deleteModuleHistory(String idModuleHistory) {
        try {
            moduleHistoryRepository.deleteById(idModuleHistory);
            return "Xóa thành công module history";
        } catch (Exception e) {
            return "xoa that bai";
        }
    }

    @Override
    public List<ModuleHistory> getModuleHistoryByWeek() {
        return null;
    }

    @Override
    public String solveEPW(Integer idProbeModule, Date timeBefore, Date timeAfter, String status) {
        try {
            Optional<Integer> error = moduleHistoryRepository.solveErrorPerWeekOfModule(idProbeModule, timeBefore, timeAfter, status);
            return "so loi = " + error.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return "khong tinh dc";
        }
    }

    // HAN
//    @Override
//    public static String solveErrorPerWeekOfModule(Integer idProbeModule, Date timeBefore, Date timeAfter, String status) {
//        try {
//            Long err = moduleHistoryRepository.solveErrorPerWeekOfModule(idProbeModule, timeBefore, timeAfter, status)
//                    .orElse(0L);
//            System.out.println(err);
//            ProbeModuleEntity probeModule = moduleProbeRepository.findById(idProbeModule).orElse(null);
////            probeModule.setErrorPerWeek(err);
//            return "so loi cua module = " + err.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "chua tinh dc loi";
//        }
//    }


//    public static void main(String[] args) {
//        Integer idProbe = 1;
//        Date timeBefore = Date.valueOf("2023-10-01");
//        Date timeAfter = Date.valueOf("2023-10-08");
//        String status = "failed";
//        String mess = null;
//        mess = solveErrorPerWeekOfModule(idProbe, timeBefore, timeAfter, status);
//        System.out.println(mess);
//    }

}
