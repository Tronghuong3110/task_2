package com.newlife.Connect_multiple.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class InfoLogin {
    private String brokerUrl;
    private String clientId;
    private Boolean cleanSession;
    private Integer connectionTimeOut;
    private Integer keepAlive;
    private String login;
}
