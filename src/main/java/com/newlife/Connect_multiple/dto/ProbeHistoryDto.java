package com.newlife.Connect_multiple.dto;

import lombok.Data;

import java.sql.Date;
// Hân
@Data
public class ProbeHistoryDto {
    private Integer id;
    private String action;
    private Date atTime;
    private String probeName;
}
