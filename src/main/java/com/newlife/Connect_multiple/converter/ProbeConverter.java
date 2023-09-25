package com.newlife.Connect_multiple.converter;

import com.newlife.Connect_multiple.dto.ProbeDto;
import com.newlife.Connect_multiple.entity.ProbeEntity;
import org.modelmapper.ModelMapper;
import org.springframework.ui.ModelMap;

public class ProbeConverter {

    public static ProbeEntity toEntity(ProbeDto probeDto) {
        try {
            ModelMapper modelMapper = new ModelMapper();
            ProbeEntity probeEntity = modelMapper.map(probeDto, ProbeEntity.class);
            probeEntity.setTotalModule(0);
            probeEntity.setNumberFailedModule(0);
            probeEntity.setNumberPendingModule(0);
            probeEntity.setNumberRunningModule(0);
            probeEntity.setNumberStopedModule(0);
            return probeEntity;
        }
        catch (Exception e) {
            System.out.print("Convert error from ProbeDto to ProbeEntity");
            e.printStackTrace();
            return new ProbeEntity();
        }
    }

    public static ProbeDto toDto(ProbeEntity entity) {
        try {
            ModelMapper modelMapper = new ModelMapper();
            ProbeDto dto = modelMapper.map(entity, ProbeDto.class);
            return dto;
        }
        catch(Exception e) {
            System.out.println("Convert error from ProbeEntity to ProbeDto ");
            return new ProbeDto();
        }
    }

    public static ProbeEntity toEntity(ProbeEntity entity, ProbeDto dto) {
        try {
            if(!dto.getName().equals(entity.getName())) {
                entity.setName(dto.getName());
            }
            if(!dto.getIpAddress().equals(entity.getIpAddress())) {
                entity.setIpAddress(dto.getIpAddress());
            }
            if(!dto.getLocation().equals(entity.getLocation())) {
                entity.setLocation(dto.getLocation());
            }
            if(!dto.getArea().equals(entity.getArea())) {
                entity.setArea(dto.getArea());
            }
            if(!dto.getDescription().equals(entity.getDescription())) {
                entity.setDescription(dto.getDescription());
            }
            return entity;
        }
        catch (NullPointerException e) {
            System.out.println("Convert from probe dto to probe entity error (Update probe)");
            e.printStackTrace();
            return null;
        }
    }
}
