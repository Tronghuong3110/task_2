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

import javax.persistence.OrderBy;
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
    public List<PerformanceCpu> findAllByTime(Integer probeId, Integer number) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime time1 = LocalDateTime.now();
            LocalDateTime beforeTime = null;

            if(number == 1) {
                beforeTime = time1.minusSeconds(6);
            }
            else {
                beforeTime = time1.minusSeconds(50);
            }
            LocalDateTime afterTime = LocalDateTime.parse(formatter.format(time1), formatter);
            beforeTime = LocalDateTime.parse(formatter.format(beforeTime), formatter);

            System.out.println("=============================================================");
            System.out.println("Before time " + formatter1.format(beforeTime));
            System.out.println("After time " + formatter1.format(afterTime));
            System.out.println("=============================================================");

            List<PerformanceCpu> jsonArray = performanceRepository.findAllByModifiedTime(beforeTime, afterTime, probeId, number);
            return jsonArray;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
