package com.newlife.Connect_multiple.entity.mysql;

import lombok.Data;

@Data
public class InfoVolumeDatabaseEntity {
    private String ipDb;
    private String type;
    private String volumeTotal;
    private String volumeUsed;
    private String volumeFree;
    private String databaseName;
}
