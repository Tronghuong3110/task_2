package com.newlife.Connect_multiple.service;

import com.newlife.Connect_multiple.dto.ModuleHistoryDto;

import java.util.List;
public interface IModuleHistoryService {
    String deleteModuleHistory(String idModuleHistory); // HAN
    List<ModuleHistoryDto> getModuleHistoryByWeek();
//    String solveErrorPerWeekOfModule(Integer idProbeModule, Date timeBefore, Date timeAfter, String status); // HAN
    String solveEPW(Integer idProbeModule, String status);

}
