package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.dto.ModuleHistory;
import com.newlife.Connect_multiple.entity.ProbeModuleEntity;
import com.newlife.Connect_multiple.repository.ModuleHistoryRepository;
import com.newlife.Connect_multiple.repository.ModuleProbeRepository;
import com.newlife.Connect_multiple.service.IModuleHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public String solveEPW(Integer idProbeModule, String status) {
        try {
            String timeBefore = getTimeBefore();
            String timeAfter = getTimeAfter();
            if(timeBefore == null || timeAfter == null) {
                System.out.println("Đếm lỗi module lồi rồi (line 50) !!!");
                return null;
            }
            Optional<Long> error = moduleHistoryRepository.solveErrorPerWeekOfModule(idProbeModule, timeBefore, timeAfter, status);
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

    // Hướng
    private String getTimeBefore() {
        try {
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return currentDate.format(formatter);
        }
        catch (Exception e) {
            System.out.println("Lấy ngày hiêện tại lỗi rồi line 77");
            e.printStackTrace();
            return null;
        }
    }
    private String getTimeAfter() {
        try {
            LocalDate currentDate = LocalDate.now();
            LocalDate sevenDateFromCurrent = currentDate.plusDays(7);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return sevenDateFromCurrent.format(formatter);
        }
        catch (Exception e) {
            System.out.println("Tính thời gian sau 7 ngày từ ngày hiện tại lỗi rồi");
            e.printStackTrace();
            return null;
        }
    }
}
