package com.newlife.Connect_multiple.dto;

import lombok.Data;


@Data
public class NasDto {
    private Integer id;
    private String nasName;
    private String username;
    private String ip;
    private String port;
    private String description;
    private String path;
    private String password;
}
