package com.newlife.Connect_multiple.converter;

import com.newlife.Connect_multiple.dto.NasDto;
import com.newlife.Connect_multiple.entity.NasEntity;
import com.newlife.Connect_multiple.util.CreateTokenUtil;

public class NasConverter {

    public static NasEntity toEntity(NasDto nasDto, NasEntity nasEntity) {
        try {
            if(nasDto.getNasName() != null) {
                System.out.println("Nas name " + nasDto.getNasName());
                nasEntity.setNasName(nasDto.getNasName());
            }
            if(nasDto.getDescription() != null) {
                nasEntity.setDescription(nasDto.getDescription());
            }
            if(nasDto.getIp() != null) {
                nasEntity.setIp(nasDto.getIp());
            }
            if(nasDto.getPath() != null) {
                nasEntity.setPath(nasDto.getPath());
            }
            if(nasDto.getPort() != null) {
                nasEntity.setPort(nasDto.getPort());
            }
            if(nasDto.getUsername() != null) {
                nasEntity.setUsername(nasDto.getUsername());
            }
            if(nasDto.getPassword() != null) {
                nasEntity.setPassword(CreateTokenUtil.enCodePass(nasDto.getPassword()));
            }
            return nasEntity;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
