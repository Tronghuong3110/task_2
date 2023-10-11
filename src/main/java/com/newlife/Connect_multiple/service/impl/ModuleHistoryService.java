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

@Service
public class ModuleHistoryService implements IModuleHistoryService {
    @Autowired
    private ModuleHistoryRepository moduleHistoryRepository;

    @Autowired
    private ModuleProbeRepository moduleProbeRepository;

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

    // HAN
    @Override
    public String solveErrorPerWeekOfModule(Integer idProbeModule, Date timeBefore, Date timeAfter, String status) {
        try {
            Integer err = moduleHistoryRepository.solveErrorPerWeekOfModule(idProbeModule, timeBefore, timeAfter, status);
            ProbeModuleEntity probeModule = moduleProbeRepository.findById(idProbeModule).orElse(null);
            probeModule.setErrorPerWeek(err);
            return "so loi cua module = " + err.toString();
        } catch (Exception e) {
            return "chua tinh dc loi";
        }
    }
}
