package com.newlife.Connect_multiple.dto;

import lombok.Data;

@Data
public class ProbeModuleDto {
    private Integer id;
    private Integer idModule;
    private Integer idProbe;
    private String path;
    private String caption;
    private String arg;
    private String command;
    private String pathLog;
    private String status;
    private Integer expectStatus;
    private String processId;
    private String note;
    private String moduleName;
    private Integer errorPerWeek;
    private Integer processStatus;
}
