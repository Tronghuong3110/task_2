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
    public String deleteModule(Integer id) {
        try {
            moduleRepository.deleteById(id);
            return "Delete module success";
        } catch (Exception e) {
            return "Delete module failed";
        }
    }

    @Override
    public String saveModule(ModuleDto moduleDto) {
        try {
            ModuleEntity moduleEntity = ModuleConverter.toEntity(moduleDto);
            String name = moduleEntity.getName();
            List<String> listModuleName = moduleRepository.getAllName();

            // check trùng module name
            if (checkModuleName(name)) {
                return "Trùng tên module";
            } else {
                moduleRepository.save(moduleEntity);
                return "Save new module success";
            }
        } catch (Exception e) {
            return "Save new module failed";
        }
    }

    @Override
    public String updateModule(ModuleDto moduleDto) {
        try {
            ModuleEntity moduleEntity = ModuleConverter.toEntity(moduleDto);
            Integer idModule = moduleEntity.getId();

            ModuleEntity module = moduleRepository.findById(idModule).orElse(null);
            String oldName = module.getName();
            String name = moduleEntity.getName();
            if (oldName.equals(name)) {
                moduleRepository.save(moduleEntity);
                return "Update module success";
            } else {
                if (checkModuleName(name)) {
                    return "Trùng tên module";
                } else {
                    moduleRepository.save(moduleEntity);
                    return "Update module success";
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Boolean checkModuleName(String name) {
        return moduleRepository.existsByName(name);
    }

    private Boolean checkIdModule(Integer id) {
        return moduleRepository.existsById(id.toString());
    }
}
