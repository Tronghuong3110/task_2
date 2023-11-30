package com.newlife.Connect_multiple.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Data
@Getter
@Setter
public class ProbeHistoryDto {
    private Integer id;
    private String action;
    private Timestamp atTime;
    private String probeName;
}
