package com.newlife.Connect_multiple.service;

import com.newlife.Connect_multiple.dto.NetworkInterfaceDto;
import com.newlife.Connect_multiple.entity.NetworkInterfaceEntity;
import org.json.simple.JSONObject;

import java.util.List;

public interface INetworkInterfaceService {
    List<NetworkInterfaceDto> findAllByProbe(Integer idProbe);
    JSONObject update(NetworkInterfaceDto interfaceDto);

}
