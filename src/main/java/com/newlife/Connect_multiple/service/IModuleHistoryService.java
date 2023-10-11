package com.newlife.Connect_multiple.service;

import com.newlife.Connect_multiple.dto.ModuleHistory;

import java.sql.Date;
import java.util.List;
public interface IModuleHistoryService {
    String deleteModuleHistory(String idModuleHistory); // HAN
    List<ModuleHistory> getModuleHistoryByWeek();
    String solveErrorPerWeekOfModule(Integer idProbeModule, Date timeBefore, Date timeAfter, String status); // HAN

}
