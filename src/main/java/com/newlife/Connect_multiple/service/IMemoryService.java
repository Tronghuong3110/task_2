package com.newlife.Connect_multiple.service;


import com.newlife.Connect_multiple.entity.PerformanceCpu;
import org.json.simple.JSONArray;

import java.util.List;

public interface IMemoryService {
    JSONArray findAllMemory(Integer probeId);

    List<PerformanceCpu> findAllByTime(Integer probeId);
}
