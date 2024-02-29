package com.newlife.Connect_multiple.dto;

import lombok.Data;

import javax.persistence.Column;


@Data
public class InfoDatabaseBackup {
    private Integer id;
    private String databaseName;
    private String timeBackup;
    private String restoreStatus;
    private Double restoreProcess;
}
