package com.newlife.Connect_multiple.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@Getter
@Setter
public class ModuleDto {
    private Integer id;
    private String name;
    private String caption;
    private String pathDefault;
    private Timestamp creatAt;
    private String argDefalt;
    private String pathLogDefault;
    private String note;


}
