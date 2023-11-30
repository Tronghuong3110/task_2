package com.newlife.Connect_multiple.service;

import com.newlife.Connect_multiple.dto.DuplicateRequest;
import com.newlife.Connect_multiple.dto.InfoLogin;
import com.newlife.Connect_multiple.dto.ProbeDto;
import com.newlife.Connect_multiple.dto.ProbeOptionDto;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public interface IProbeService {
    ProbeDto findOneProbe(Integer idProbe);
    ProbeDto saveProbe(ProbeDto probeDto, ProbeOptionDto probeOptionDto);
    List<ProbeDto> findAllProbe(String name, String location, String area, String vlan);
    JSONObject delete(Integer id);
    JSONObject updateProbe(ProbeDto probeDto);
    JSONArray backUpProbe(List<Integer> ids);
    InfoLogin downloadFile(Integer idProbe);
    Integer countProbeByStatus(String status);
    JSONArray deleteProbe(List<Integer> ids);
    List<ProbeDto> findAllProbeByDeleted(String name, Integer page);
    JSONArray duplicate(DuplicateRequest duplicateRequest);
}
