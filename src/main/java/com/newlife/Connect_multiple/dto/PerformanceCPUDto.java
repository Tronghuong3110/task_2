package com.newlife.Connect_multiple.dto;

import lombok.Data;

import javax.persistence.Column;
import java.sql.Timestamp;

@Data
public class PerformanceCPUDto {
    private Integer id;
    private Double loadAverage;
    private Integer probeId;
    private Timestamp modifiedTime;
}
