package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.dto.ModuleHistoryDto;
import com.newlife.Connect_multiple.repository.ModuleHistoryRepository;
import com.newlife.Connect_multiple.repository.ModuleProbeRepository;
import com.newlife.Connect_multiple.service.IModuleHistoryService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ModuleHistoryService implements IModuleHistoryService {

    @Autowired
    private static ModuleHistoryRepository moduleHistoryRepository;

    // HAN
    @Override
    public JSONObject deleteModuleHistory(String idModuleHistory) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", "1");
            jsonObject.put("message", "Xóa thành công module history");
            moduleHistoryRepository.deleteById(idModuleHistory);
            return jsonObject;
        } catch (Exception e) {
            jsonObject.put("code", "0");
            jsonObject.put("message", "xoa that bai");
            return jsonObject;
        }
    }

    @Override
    public List<ModuleHistoryDto> getModuleHistoryByWeek() {
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
            System.out.println("Lỗi tính error (line 54) ");
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
