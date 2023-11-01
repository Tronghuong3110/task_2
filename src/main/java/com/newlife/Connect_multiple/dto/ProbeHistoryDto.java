package com.newlife.Connect_multiple.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
public class ProbeHistoryDto {
    private Integer id;
    private String action;
    private Date atTime;
    private String probeName;
}
