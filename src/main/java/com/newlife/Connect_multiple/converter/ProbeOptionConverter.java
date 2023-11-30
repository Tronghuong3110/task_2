package com.newlife.Connect_multiple.converter;

import com.newlife.Connect_multiple.dto.ProbeOptionDto;
import com.newlife.Connect_multiple.entity.ProbeOptionEntity;
import org.modelmapper.ModelMapper;

public class ProbeOptionConverter {

    public static ProbeOptionEntity toEntity(ProbeOptionDto probeOptionDto) {
        try {
            ModelMapper modelMapper = new ModelMapper();
            ProbeOptionEntity entity = modelMapper.map(probeOptionDto, ProbeOptionEntity.class);
            return entity;
        }
        catch (Exception e) {
            System.out.println("Convert error from ProbeOptionDto to ProbeOptionEntity");
            e.printStackTrace();
            return new ProbeOptionEntity();
        }
    }

    public static ProbeOptionDto toDto(ProbeOptionEntity entity) {
        ModelMapper modelMapper = new ModelMapper();
        ProbeOptionDto dto = modelMapper.map(entity, ProbeOptionDto.class);
        return dto;
    }

    public static ProbeOptionEntity toEntity(ProbeOptionEntity probeOption1) {
        try {
            ModelMapper mapper = new ModelMapper();
            ProbeOptionEntity probeOptionEntity = mapper.map(probeOption1, ProbeOptionEntity.class);
            probeOptionEntity.setId(null);
            return probeOptionEntity;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ProbeOptionEntity();
        }
    }
}
