package com.newlife.Connect_multiple.service;

import java.util.List;

public interface IProbeHistoryService {
    List<ProbeHistoryDto> getLastNRecord(Integer n);
}
