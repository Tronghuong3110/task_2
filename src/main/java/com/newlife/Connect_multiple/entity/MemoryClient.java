package com.newlife.Connect_multiple.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "memory")
public class MemoryClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "disk_name")
    private String diskName;
    @Column(name = "memory_disk")
    private Double memoryDisk;
    @Column(name = "total_memory")
    private Double totalMemory;
    @Column(name = "probe_id")
    private Integer probeId;
    @Column(name = "modified_time")
    private Timestamp modifiedTime;
}
