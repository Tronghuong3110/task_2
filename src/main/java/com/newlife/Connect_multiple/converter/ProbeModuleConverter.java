package com.newlife.Connect_multiple.converter;

import com.newlife.Connect_multiple.dto.ProbeModuleDto;
import com.newlife.Connect_multiple.entity.ProbeModuleEntity;
import org.modelmapper.ModelMapper;
import org.springframework.ui.Model;

public class ProbeModuleConverter {

    public static ProbeModuleDto toDto(ProbeModuleEntity entity) {
        try {
            ModelMapper modelMapper = new ModelMapper();
            ProbeModuleDto probeModuleDto = modelMapper.map(entity, ProbeModuleDto.class);
            return probeModuleDto;
        }
        catch (Exception e) {
            System.out.println("Convert from ProbeModuleEntity to ProbeModuleDto error");
            e.printStackTrace();
            return new ProbeModuleDto();
        }
    }

    public static ProbeModuleEntity toEntity(ProbeModuleDto dto) {
        try {
            ModelMapper modelMapper = new ModelMapper();
            ProbeModuleEntity entity = modelMapper.map(dto, ProbeModuleEntity.class);
            return entity;
        }
        catch (Exception e) {
            System.out.println("Convert from ProbeModuleDto to ProbeModuleEntity error");
            e.printStackTrace();
            return new ProbeModuleEntity();
        }
    }
}
