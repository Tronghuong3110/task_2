package com.newlife.Connect_multiple.service;

import com.newlife.Connect_multiple.dto.ProbeDto;
import com.newlife.Connect_multiple.dto.ProbeOptionDto;
import java.util.*;

public interface IProbeService {
    ProbeDto saveProbe(ProbeDto probeDto, ProbeOptionDto probeOptionDto);
    List<ProbeDto> findAllProbe(String name, String location, String area, String vlan, String sortBy, Integer page);
    String delete(Integer id);
    String updateProbe(ProbeDto probeDto);
    String backUpProbe(Integer id);
}
