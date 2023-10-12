package com.newlife.Connect_multiple.service;

import com.newlife.Connect_multiple.dto.ProbeHistoryDto;

import java.util.List;

public interface IProbeHistoryService {
    List<ProbeHistoryDto> getLastNRecord(Integer n);
}
