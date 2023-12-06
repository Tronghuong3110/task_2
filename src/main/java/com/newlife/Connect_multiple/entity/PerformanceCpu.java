package com.newlife.Connect_multiple.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "performance_cpu")
public class PerformanceCpu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "load_average")
    private Double loadAverage;
    @Column(name = "probe_id")
    private Integer probeId;
    @Column(name = "modified_time")
    private Timestamp modifiedTime;
    @Column(name = "message")
    private String message;
}
