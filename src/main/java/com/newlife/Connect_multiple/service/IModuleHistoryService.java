package com.newlife.Connect_multiple.service;

import com.newlife.Connect_multiple.dto.ModuleHistoryDto;
import org.json.simple.JSONObject;

import java.util.List;
public interface IModuleHistoryService {
    JSONObject deleteModuleHistory(List<String> ids); // HAN
    List<ModuleHistoryDto> findAllModuleHistoryByCondition(Integer idProbeModule, Integer idProbe, String timeStart, String timeEnd, Integer ack, String content, Integer page);  // Tên module / tên probe / thời gian / đã được ACK hay chưa
    Integer updateAck(String id);
}
