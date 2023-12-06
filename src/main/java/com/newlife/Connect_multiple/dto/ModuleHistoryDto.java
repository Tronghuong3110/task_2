package com.newlife.Connect_multiple.dto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
public class ModuleHistoryDto {
    private String idModuleHistory;
    private Integer idProbe;
    private String content;
    private String title;
    private String caption;
    private String arg;
    private String status;
    private String atTime;
    private String moduleName;
    private String note;
    private String probeName;
    private Integer ack;
    private Long totalPage;
}