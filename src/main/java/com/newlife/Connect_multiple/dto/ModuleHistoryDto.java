package com.newlife.Connect_multiple.dto;
import lombok.Data;

import java.sql.Date;

@Data
public class ModuleHistoryDto {
    private String idModuleHistory;
    private Integer idProbe;
    private String content;
    private String title;
    private String caption;
    private String arg;
    private String status;
    private Date atTime;
    private String moduleName;
    private String note;
}