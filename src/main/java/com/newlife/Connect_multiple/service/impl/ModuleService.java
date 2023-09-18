package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.dto.ModuleDto;
import com.newlife.Connect_multiple.service.IModuleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleService implements IModuleService {
    @Override
    public List<ModuleDto> findAllModule(String name) {
        return null;
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
