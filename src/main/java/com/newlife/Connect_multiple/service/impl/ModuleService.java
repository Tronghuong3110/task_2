package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.converter.ModuleConverter;
import com.newlife.Connect_multiple.dto.ModuleDto;
import com.newlife.Connect_multiple.entity.ModuleEntity;
import com.newlife.Connect_multiple.repository.ModuleRepository;
import com.newlife.Connect_multiple.service.IModuleService;
import org.json.simple.JSONObject;
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
        ModuleEntity moduleEntity = moduleRepository.findById(id).orElse(new ModuleEntity());
        return ModuleConverter.toDto(moduleEntity);
    }

    @Override
    public JSONObject deleteModule(Integer id) {
        JSONObject json = new JSONObject();
        try {
            moduleRepository.deleteById(id);
            json.put("code", "1");
            json.put("message", "Delete module success");
            return json;
        } catch (Exception e) {
            json.put("code", "0");
            json.put("message", "Delete module failed");
            return json;
        }
    }

    @Override
    public JSONObject saveModule(ModuleDto moduleDto) {
        JSONObject json = new JSONObject();
        try {
            ModuleEntity moduleEntity = ModuleConverter.toEntity(moduleDto);
            String name = moduleEntity.getName();

            // check trùng module name
            if (checkModuleName(name)) {
                json.put("code", "3");
                json.put("message", "Trùng tên module");
                return json;
            } else {
                moduleRepository.save(moduleEntity);
                json.put("code", "1");
                json.put("message", "Save new module success");
                return json;
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.put("code", 0);
            json.put("message", "Save new module failed");
            return json;
        }
    }

    @Override
    public JSONObject updateModule(ModuleDto moduleDto) {
        JSONObject json = new JSONObject();
        try {
            // lấy ra module theo id từ database
            ModuleEntity moduleEntity = moduleRepository.findById(moduleDto.getId()).orElse(null);
            moduleEntity = ModuleConverter.toEntity(moduleEntity, moduleDto);
            // Th không tồn tại module theo id
            if(moduleEntity == null) {
                json.put("code", "0");
                json.put("message", "Update module fail");
                return json;
            }
            // lấy module theo tên module
            ModuleEntity module = moduleRepository.findByName(moduleDto.getName()).orElse(null);
            // module mới có id trùng mới module cũ
            // nếu khác ==> trùng tên ==> không cho cập nhật
            if(module != null && module.getId() != moduleEntity.getId()) {
                json.put("code", "3");
                json.put("message", "Module name has been duplicated");
                return json;
            }
            else {
                moduleEntity.setName(moduleDto.getName());
                moduleRepository.save(moduleEntity);
                json.put("code", "1");
                json.put("message", "Update module success");
                return json;
            }
//            Integer idModule = moduleEntity.getId();
//            String oldName = module.getName();
//            String newName = moduleEntity.getName();
//            if (oldName.equals(newName)) {
//                moduleRepository.save(moduleEntity);
//                json.put("code", "1");
//                json.put("message", "Update module success");
//                return json;
//            } else {
//                if (checkModuleName(newName)) {
//                    json.put("code", "3");
//                    json.put("message", "Trùng tên module");
//                    return json;
//                } else {
//                    moduleRepository.save(moduleEntity);
//                    json.put("code", "1");
//                    json.put("message", "Update module success");
//                    return json;
//                }
//            }
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
