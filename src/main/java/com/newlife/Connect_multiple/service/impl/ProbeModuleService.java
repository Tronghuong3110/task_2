package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.converter.ProbeModuleConverter;
import com.newlife.Connect_multiple.dto.ProbeModuleDto;
import com.newlife.Connect_multiple.entity.ProbeModuleEntity;
import com.newlife.Connect_multiple.repository.ModuleProbeRepository;
import com.newlife.Connect_multiple.service.IProbeModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProbeModuleService implements IProbeModuleService {

    @Autowired
    private ModuleProbeRepository moduleProbeRepository;
    @Override
    public List<ProbeModuleDto> findAllProbeModule(String moduleName, String status, Integer page, String sortBy) {
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
            Pageable pageable = PageRequest.of(page, 1, sort);
            Page<ProbeModuleEntity> listProbeModuleEntity = moduleProbeRepository.findAllByProbeNameOrStatus(moduleName, status, pageable);
            List<ProbeModuleDto> listProbeModuleDto = new ArrayList<>();
            for(ProbeModuleEntity entity : listProbeModuleEntity.toList()) {
                listProbeModuleDto.add(ProbeModuleConverter.toDto(entity));
            }
            return listProbeModuleDto;
        }
        catch (Exception e) {
            System.out.println("Find All ProbeModule error");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
