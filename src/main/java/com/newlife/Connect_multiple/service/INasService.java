package com.newlife.Connect_multiple.service;

import com.newlife.Connect_multiple.dto.NasDto;
import com.newlife.Connect_multiple.entity.NasEntity;
import org.json.simple.JSONObject;

import java.util.List;

public interface INasService {
    JSONObject saveNas(NasDto nasDto);
    List<NasDto> findAllNas();
    JSONObject deleteNas(Integer idNas);
    JSONObject updateNas(NasDto nasDto);
    NasDto findOne(Integer id);
    JSONObject testConnectFtp(String ip, String username, String pass, Integer port);
}
