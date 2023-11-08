package com.newlife.Connect_multiple.converter;

import com.newlife.Connect_multiple.dto.TypeModuleDto;
import com.newlife.Connect_multiple.entity.TypeModuleEntity;
import org.modelmapper.ModelMapper;

public class TupeModuleConverter {
    public static TypeModuleDto toDto(TypeModuleEntity typeModule) {
        try {
            ModelMapper mapper = new ModelMapper();
            TypeModuleDto typeModuleDto = mapper.map(typeModule, TypeModuleDto.class);
            return typeModuleDto;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
