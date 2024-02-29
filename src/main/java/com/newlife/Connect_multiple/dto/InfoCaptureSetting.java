package com.newlife.Connect_multiple.dto;

import lombok.Data;

@Data
public class InfoCaptureSetting {
    private Integer id_info_capture_setting;
    private String probeName;
    private String dbName;
    private String province;
    private String ipDbLevel1;
    private String ipDbLevel2;
    private String ipDbRunning;
    private String statusMonitor;
    private Long totalVolume;
    private String startTime;
    private String stopTime;
    private String backupStatus;
    private Integer idServer;
    private Integer nasId;
}
