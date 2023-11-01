package com.newlife.Connect_multiple.service;

import com.newlife.Connect_multiple.dto.ModuleHistoryDto;
import org.json.simple.JSONObject;

import java.util.List;
public interface IModuleHistoryService {
    JSONObject deleteModuleHistory(String idModuleHistory); // HAN
    List<ModuleHistoryDto> findAllModuleHistoryByCondition(Integer idProbeModule, Integer idProbe, String time, Integer ack, Integer page);  // Tên module / tên probe / thời gian / đã được ACK hay chưa
//    String solveErrorPerWeekOfModule(Integer idProbeModule, Date timeBefore, Date timeAfter, String status); // HAN
    String solveEPW(Integer idProbeModule, String status);
    Integer updateAck(String id);

}
