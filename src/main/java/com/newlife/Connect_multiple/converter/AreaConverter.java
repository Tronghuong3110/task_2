package com.newlife.Connect_multiple.converter;

import com.newlife.Connect_multiple.dto.AreaDto;
import com.newlife.Connect_multiple.entity.AreaEntity;
import org.modelmapper.ModelMapper;

public class AreaConverter {

    public static AreaDto toDto(AreaEntity entity) {
        ModelMapper modelMapper = new ModelMapper();
        AreaDto dto = modelMapper.map(entity, AreaDto.class);
        return dto;
    }
}
