package com.newlife.Connect_multiple.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ProbeOptionDto {

    private Integer id;
    private Integer keepAlive;
    private Boolean cleanSession;
    private String username;
    private String password;
    private Integer connectionTimeOut;


}
