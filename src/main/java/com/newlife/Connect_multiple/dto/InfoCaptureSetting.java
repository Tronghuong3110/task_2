package com.newlife.Connect_multiple.dto;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "info_capture_setting")
public class InfoCaptureSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer id_info_capture_setting;
    private String probeName;
    private String dbName;
    private String province;
    private String ipDbLevel1;
    private String ipDbLevel2;
    private String ipDbRunning;
    private String statusMonitor;
    private String totalVolume;
    private String startTime;
    private String stopTime;
    private String backupStatus;
    @Column(name = "id_server")
    private Integer idServer;
    private Integer nasId;
    private String message;
    private Integer status_connect;
    private String statusRestore;
    private Double processRestore;
    private Double processBackup;
}
