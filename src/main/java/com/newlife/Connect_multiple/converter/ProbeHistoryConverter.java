package com.newlife.Connect_multiple.converter;

import com.newlife.Connect_multiple.dto.ProbeHistoryDto;
import com.newlife.Connect_multiple.entity.ProbeHistoryEntity;
import org.modelmapper.ModelMapper;


// Hân
public class ProbeHistoryConverter {
    public static ProbeHistoryDto toDto(ProbeHistoryEntity entity) {
        try {
            ModelMapper modelMapper = new ModelMapper();
            ProbeHistoryDto probeHistoryDto = modelMapper.map(entity, ProbeHistoryDto.class);
            return probeHistoryDto;
        } catch (Exception e) {
            System.out.println("Convert from ProbeModuleEntity to ProbeModuleDto error");
            e.printStackTrace();
            return new ProbeHistoryDto();
        }
    }

    public static ProbeHistoryEntity toEntity(ProbeHistoryDto dto) {
        try {
            ModelMapper modelMapper = new ModelMapper();
            ProbeHistoryEntity entity = modelMapper.map(dto, ProbeHistoryEntity.class);
            return entity;
        } catch (Exception e) {
            System.out.println("Convert from ProbeModuleDto to ProbeModuleEntity error");
            e.printStackTrace();
            return new ProbeHistoryEntity();
        }
    }
}
