package com.newlife.Connect_multiple.converter;

import com.newlife.Connect_multiple.dto.AreaDto;
import com.newlife.Connect_multiple.dto.LocationDto;
import com.newlife.Connect_multiple.entity.AreaEntity;
import com.newlife.Connect_multiple.entity.LocationEntity;
import org.modelmapper.ModelMapper;
import java.util.*;

public class LocationConverter {
    public static LocationDto toDto(LocationEntity entity) {
        ModelMapper modelMapper = new ModelMapper();
        LocationDto dto = modelMapper.map(entity, LocationDto.class);
        List<AreaDto> listAreas = new ArrayList<>();
        for(AreaEntity areaEntity : entity.getAreaEntityList()) {
            AreaDto area = AreaConverter.toDto(areaEntity);
            listAreas.add(area);
        }
        dto.setListArea(listAreas);
        return dto;
    }

    private static List<AreaDto> converAreaDto(List<AreaEntity> entities) {
        List<AreaDto> listDto = new ArrayList<>();
        for(AreaEntity entity : entities) {
            AreaDto area = AreaConverter.toDto(entity);
            listDto.add(area);
        }
        return listDto;
    }
}
