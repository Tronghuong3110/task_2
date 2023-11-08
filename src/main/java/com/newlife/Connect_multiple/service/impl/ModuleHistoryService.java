package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.converter.ModuleHistoryConverter;
import com.newlife.Connect_multiple.dto.ModuleHistoryDto;
import com.newlife.Connect_multiple.entity.ModuleHistoryEntity;
import com.newlife.Connect_multiple.repository.ModuleHistoryRepository;
import com.newlife.Connect_multiple.repository.ModuleProbeRepository;
import com.newlife.Connect_multiple.service.IModuleHistoryService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ModuleHistoryService implements IModuleHistoryService {

    @Autowired
    private ModuleHistoryRepository moduleHistoryRepository;

    // HAN
    @Override
    public JSONObject deleteModuleHistory(List<String> ids) {
        JSONObject jsonObject = new JSONObject();
        try {
            for(String id : ids) {
                moduleHistoryRepository.deleteById(id);
            }

            jsonObject.put("code", "1");
            jsonObject.put("message", "Delete module history success");
            return jsonObject;
        } catch (Exception e) {
            jsonObject.put("code", "0");
            jsonObject.put("message", "Delete fail");
            return jsonObject;
        }
    }

    @Override
    public List<ModuleHistoryDto> findAllModuleHistoryByCondition(Integer idProbeModule, Integer idProbe, String timeStart, String timeEnd, Integer ack, String content, Integer page) {
        Sort sort = Sort.by("at_time").descending();
        Pageable pageable = PageRequest.of(page, 10, sort);
        Page<ModuleHistoryEntity> listModuleHistories = moduleHistoryRepository.findAllByCondition(idProbeModule, idProbe, timeStart, timeEnd, content, ack, pageable);
        List<ModuleHistoryDto> listModuleHistoriesDto = new ArrayList<>();
        Long totalRow = moduleHistoryRepository.count(idProbeModule, idProbe, timeStart, timeEnd, content, ack);
        Long totalPage = Math.round(((double)totalRow) / 10);
        if(totalPage < totalRow / 10) {
            totalPage += 1;
        }
        for(ModuleHistoryEntity moduleHistory : listModuleHistories.toList()) {
            ModuleHistoryDto dto = ModuleHistoryConverter.toDto(moduleHistory);
            dto.setTotalPage(totalPage);
            listModuleHistoriesDto.add(dto);
        }
        return listModuleHistoriesDto;
    }

    @Override
    public Integer updateAck(String id) {
        try {
            ModuleHistoryEntity moduleHistory = moduleHistoryRepository.findById(id).orElse(null);
            if(moduleHistory == null) {
                return 0;
            }
            // ack = 1: đã xác nhận xem
            // ack = 0: chưa xác nhận xem
            System.out.println("ACK trước khi cập nhật " + moduleHistory.getAck());
            if(!moduleHistory.getAck().equals(1)) {
                moduleHistory.setAck(1);
                moduleHistory = moduleHistoryRepository.save(moduleHistory);
                System.out.println("ACK sau khi cập nhật" + moduleHistory.getAck());
                return 1;
            }
            return 1;
        }
        catch (Exception e) {
            System.out.println("Update ack error!");
            e.printStackTrace();
            return 0;
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
}
