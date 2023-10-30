package com.newlife.Connect_multiple.service;

import com.newlife.Connect_multiple.dto.ProbeModuleDto;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public interface IProbeModuleService {
    List<ProbeModuleDto> findAllProbeModule(String moduleName, String status, Integer idProbe);
    Object runModule(Integer idProbeModule);
    Object stopModule(Integer idProbeModule);
    void getStatusModulePeriodically();
    JSONObject delete(List<String> ids); // Han
    JSONObject saveProbeModule(ProbeModuleDto probeModuleDto);
    Integer countModuleByStatus(String status);
    ProbeModuleDto findOneById(Integer idProbeModule);
    JSONObject updateProbeModule(ProbeModuleDto probeModuleDto);


}
