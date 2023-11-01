package com.newlife.Connect_multiple.converter;

import com.newlife.Connect_multiple.dto.ModuleDto;
import com.newlife.Connect_multiple.entity.ModuleEntity;
import org.modelmapper.ModelMapper;

public class ModuleConverter {

    public static ModuleDto toDto(ModuleEntity moduleEntity) {
        try {
            ModelMapper modelMapper = new ModelMapper();
            ModuleDto moduleDto = modelMapper.map(moduleEntity, ModuleDto.class);
            return moduleDto;
        }
        catch (Exception e) {
            System.out.println("Convert from module entity to module dto error");
            e.printStackTrace();
            return null;
        }
    }

    public static ModuleEntity toEntity(ModuleDto moduleDto) {
        try {
            ModelMapper modelMapper = new ModelMapper();
            ModuleEntity moduleEntity = modelMapper.map(moduleDto, ModuleEntity.class);
            return moduleEntity;
        }
        catch (Exception e) {
            System.out.println("Convert from module dto to module entity error");
            e.printStackTrace();
            return null;
        }
    }

    public static ModuleEntity toEntity(ModuleEntity moduleEntity, ModuleDto moduleDto) {
        try {
            if(moduleDto.getPathDefault() != null && !moduleDto.getPathDefault().equals(moduleEntity.getPathDefault())) {
                moduleEntity.setPathDefault(moduleDto.getPathDefault());
            }
            if(moduleDto.getNote() != null && !moduleDto.getNote().equals(moduleEntity.getNote())) {
                moduleEntity.setNote(moduleDto.getNote());
            }
            if(moduleDto.getArgDefalt() != null && !moduleDto.getArgDefalt().equals(moduleEntity.getArgDefalt())) {
                moduleEntity.setArgDefalt(moduleDto.getArgDefalt());
            }
            if(moduleDto.getPathLogDefault() != null && !moduleDto.getPathLogDefault().equals(moduleEntity.getPathLogDefault())) {
                moduleEntity.setPathLogDefault(moduleDto.getPathLogDefault());
            }
            return moduleEntity;
        }
        catch(NullPointerException e) {
            System.out.println("Convert from module dto to module entity error (update module)");
            e.printStackTrace();
            return null;
        }
    }
}
