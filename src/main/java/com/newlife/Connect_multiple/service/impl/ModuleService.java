package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.converter.ModuleConverter;
import com.newlife.Connect_multiple.dto.ModuleDto;
import com.newlife.Connect_multiple.entity.ModuleEntity;
import com.newlife.Connect_multiple.repository.ModuleRepository;
import com.newlife.Connect_multiple.service.IModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ModuleService implements IModuleService {

    @Autowired
    private ModuleRepository moduleRepository;

    @Override
    public List<ModuleDto> findAllModule(String name) {
        List<ModuleEntity> listModules = moduleRepository.findAllByName(name);
        List<ModuleDto> listModuleDto = new ArrayList<>();
        for(ModuleEntity entity : listModules) {
            listModuleDto.add(ModuleConverter.toDto(entity));
        }
        return listModuleDto;
    }

    @Override
    public ModuleDto findOneModule(Integer id) {
        return null;
    }

    @Override
    public void deleteModule(Integer id) {

    }

    @Override
    public ModuleDto saveModule(ModuleDto moduleDto) {
        return null;
    }
}
