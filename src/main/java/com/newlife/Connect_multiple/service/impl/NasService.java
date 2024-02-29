package com.newlife.Connect_multiple.service.impl;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.newlife.Connect_multiple.converter.NasConverter;
import com.newlife.Connect_multiple.dto.NasDto;
import com.newlife.Connect_multiple.entity.NasEntity;
import com.newlife.Connect_multiple.repository.NasRepository;
import com.newlife.Connect_multiple.service.INasService;
import com.newlife.Connect_multiple.util.CreateTokenUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NasService implements INasService {

    @Autowired
    private NasRepository nasRepository;

    @Override
    public JSONObject saveNas(NasDto nasDto) {
        JSONObject response = new JSONObject();
        try {
            Boolean checkIp = nasRepository.existsByIp(nasDto.getIp());
            if(checkIp) {
                response.put("code", 0);
                response.put("message", "Ip has been duplicated");
                return response;
            }
            NasEntity nas = new NasEntity();
            BeanUtils.copyProperties(nasDto, nas);
            nas.setPassword(CreateTokenUtil.enCodePass(nasDto.getPassword()));
            nasRepository.save(nas);
            response.put("code", 1);
            response.put("message", "Add new nas success");
        }
        catch (Exception e) {
            e.printStackTrace();
            response.put("code", 0);
            response.put("message", "Add new nas error");
        }
        return response;
    }

    @Override
    public List<NasDto> findAllNas() {
        try {
            List<NasEntity> listNasEntity = nasRepository.findAll();
            List<NasDto> listResponse = new ArrayList<>();
            for(NasEntity nas : listNasEntity) {
                NasDto nasDto = new NasDto();
                BeanUtils.copyProperties(nas, nasDto);
                listResponse.add(nasDto);
            }
            return listResponse;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public JSONObject deleteNas(Integer idNas) {
        JSONObject response = new JSONObject();
        try {
            nasRepository.deleteById(idNas);
            response.put("code", 1);
            response.put("message", "Delete nas server success");
        }
        catch (Exception e) {
            e.printStackTrace();
            response.put("code", 0);
            response.put("message", "Can not delete nas server");
        }
        return response;
    }

    @Override
    public JSONObject updateNas(NasDto nasDto) {
        JSONObject response = new JSONObject();
        try {
            NasEntity nasEntity = nasRepository.findByIp(nasDto.getIp()).orElse(null);
            // Th không phải nas server đang chỉnh sửa
            if(nasEntity == null || nasEntity.getId().equals(nasDto.getId())) {
                nasEntity = NasConverter.toEntity(nasDto, nasEntity);
                if(nasEntity == null) {
                    response.put("code", 0);
                    response.put("message", "Can not update nas server");
                    return response;
                }
                nasRepository.save(nasEntity);
                response.put("code", 1);
                response.put("message", "Update nas server success");
                return response;
            }
            response.put("code", 0);
            response.put("message", "Can not update nas server because of duplicated ip");
            return response;
        }
        catch (Exception e) {
            e.printStackTrace();
            response.put("code", 0);
            response.put("message", "Update nas server error");
        }
        return response;
    }

    @Override
    public NasDto findOne(Integer id) {
        try {
            NasEntity nas = nasRepository.findById(id).orElse(null);
            NasDto nasDto = new NasDto();
            if(nas == null) {
                return null;
            }
            BeanUtils.copyProperties(nas, nasDto);
            return nasDto;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new NasDto();
        }
    }
}
