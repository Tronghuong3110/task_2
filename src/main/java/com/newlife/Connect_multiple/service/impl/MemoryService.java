package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.entity.PerformanceCpu;
import com.newlife.Connect_multiple.repository.MemoryRepository;
import com.newlife.Connect_multiple.repository.PerformanceRepository;
import com.newlife.Connect_multiple.service.IMemoryService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class MemoryService implements IMemoryService {
    @Autowired
    MemoryRepository memoryRepository;
    @Autowired
    private PerformanceRepository performanceRepository;
    @Override
    public JSONArray findAllMemory(Integer probeId) {
        try {
            JSONArray jsonArray = memoryRepository.findAllMemory(probeId);
            JSONParser parser = new JSONParser();
            for(Object object : jsonArray) {
                JSONObject jsonObject = (JSONObject) parser.parse(object.toString());
                return (JSONArray) jsonObject.get("memories");
            }
            return new JSONArray();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<PerformanceCpu> findAllByTime(Integer probeId) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime time1 = LocalDateTime.now();
            String beforeTime = formatter.format(time1.minusMinutes(10));
            String afterTime = formatter.format(time1);
//            String beforeTime = "2023-12-08 15:39:00";
//            String afterTime = "2023-12-08 15:42:55";
            List<PerformanceCpu> jsonArray = performanceRepository.findAllByModifiedTime(beforeTime, afterTime, probeId);
            return jsonArray;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
