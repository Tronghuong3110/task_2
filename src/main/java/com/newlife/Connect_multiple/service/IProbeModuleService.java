package com.newlife.Connect_multiple.service;

import com.newlife.Connect_multiple.dto.ProbeModuleDto;
import org.json.simple.JSONObject;

import java.util.List;

public interface IProbeModuleService {
    List<ProbeModuleDto> findAllProbeModule(String moduleName, String status, Integer page, String sortBy);
    String runModule(Integer idProbeModule);
}
