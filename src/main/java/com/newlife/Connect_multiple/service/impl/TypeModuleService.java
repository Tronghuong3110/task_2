package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.converter.TupeModuleConverter;
import com.newlife.Connect_multiple.dto.TypeModuleDto;
import com.newlife.Connect_multiple.entity.TypeModuleEntity;
import com.newlife.Connect_multiple.repository.TypeModuleRepository;
import com.newlife.Connect_multiple.service.ITypeModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TypeModuleService implements ITypeModuleService {
    @Autowired
    private TypeModuleRepository typeModuleRepository;
    @Override
    public List<TypeModuleDto> findAll() {
        try {
            List<TypeModuleEntity> typeModuleEntityList = typeModuleRepository.findAll();
            List<TypeModuleDto> typeModuleDtoList = new ArrayList<>();
            for(TypeModuleEntity typeModule : typeModuleEntityList) {
                typeModuleDtoList.add(TupeModuleConverter.toDto(typeModule));
            }
            return typeModuleDtoList;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
