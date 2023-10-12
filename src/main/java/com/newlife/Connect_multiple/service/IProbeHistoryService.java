package com.newlife.Connect_multiple.service;

import com.newlife.Connect_multiple.entity.ProbeHistoryEntity;

import java.util.List;

public interface IProbeHistoryService {
    List<ProbeHistoryEntity> getLastNRecord(Integer n);
}
