package com.newlife.Connect_multiple.service;

import com.newlife.Connect_multiple.dto.ProbeModuleDto;

import java.util.List;
import java.util.Map;

public interface IProbeModuleService {
    List<ProbeModuleDto> findAllProbeModule(String moduleName, String status, Integer idProbe);
    Object runModule(Integer idProbeModule);
    Object stopModule(Integer idProbeModule);
    void getStatusModulePeriodically();
    String delete(Integer idProbeModule); // Han
    String saveProbeModule(ProbeModuleDto probeModuleDto);
    Integer countModuleByStatus(String status);
    ProbeModuleDto findOneById(Integer idProbeModule);
    String updateProbeModule(ProbeModuleDto probeModuleDto);
}
