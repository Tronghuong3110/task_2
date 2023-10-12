package com.newlife.Connect_multiple.converter;

import com.newlife.Connect_multiple.dto.ModuleHistoryDto;
import com.newlife.Connect_multiple.entity.ModuleHistoryEntity;
import org.modelmapper.ModelMapper;

// Hân
public class ModuleHistoryConverter {
    public static ModuleHistoryDto toDto(ModuleHistoryEntity entity) {
        try {
            ModelMapper modelMapper = new ModelMapper();
            ModuleHistoryDto moduleHistoryDto = modelMapper.map(entity, ModuleHistoryDto.class);
            return moduleHistoryDto;
        } catch (Exception e) {
            System.out.println("Convert from ModuleHistoryEntity to ModuleHistoryDto error");
            e.printStackTrace();
            return new ModuleHistoryDto();
        }
    }

    public static ModuleHistoryEntity toEntity(ModuleHistoryDto dto) {
        try {
            ModelMapper modelMapper = new ModelMapper();
            ModuleHistoryEntity entity = modelMapper.map(dto, ModuleHistoryEntity.class);
            return entity;
        } catch (Exception e) {
            System.out.println("Convert from ModuleHistoryDto to ModuleHistoryEntity error");
            e.printStackTrace();
            return new ModuleHistoryEntity();
        }
    }
}
