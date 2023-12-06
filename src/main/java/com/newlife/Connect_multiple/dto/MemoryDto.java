package com.newlife.Connect_multiple.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MemoryDto {
    private Integer id;
    private String diskName;
    private Double memoryDisk;
    private Double totalMemory;
    private Integer probeId;
    private Timestamp modifiedTime;
    private Double percent;
}
