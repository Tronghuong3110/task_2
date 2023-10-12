package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.converter.ProbeHistoryConverter;
import com.newlife.Connect_multiple.dto.ProbeHistoryDto;
import com.newlife.Connect_multiple.entity.ProbeHistoryEntity;
import com.newlife.Connect_multiple.repository.ProbeHistoryRepository;
import com.newlife.Connect_multiple.service.IProbeHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProbeHistoryService implements IProbeHistoryService {

    @Autowired
    private ProbeHistoryRepository probeHistoryRepository;

    @Override
    public List<ProbeHistoryDto> getLastNRecord(Integer n) {
        List<ProbeHistoryDto> probeHistoryDtoList = new ArrayList<>();
        try {
            List<ProbeHistoryEntity> probeHistoryList = probeHistoryRepository.findLastNRecord(n);

            for (ProbeHistoryEntity probeHistory : probeHistoryList) {
                probeHistoryDtoList.add(ProbeHistoryConverter.toDto(probeHistory));
            }
            return probeHistoryDtoList;
        } catch (Exception e) {
            return probeHistoryDtoList;
        }
    }
}
