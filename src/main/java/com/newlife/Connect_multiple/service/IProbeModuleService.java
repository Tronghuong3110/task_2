package com.newlife.Connect_multiple.service;

import com.newlife.Connect_multiple.dto.ProbeModuleDto;

import java.util.List;

public interface IProbeModuleService {
    List<ProbeModuleDto> findAllProbeModule(String moduleName, String status, Integer page, String sortBy);
    Object runModule(Integer idProbeModule);
    Object stopModule(Integer idProbeModule);

    void getStatusModulePeriodically();
}
