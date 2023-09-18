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


}
