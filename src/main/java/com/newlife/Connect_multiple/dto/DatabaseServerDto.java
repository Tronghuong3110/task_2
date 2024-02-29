package com.newlife.Connect_multiple.dto;

import lombok.Data;

@Data
public class DatabaseServerDto {
    private Integer id;
    private String ipServer;
    private String serverName;
    private String type;
    private String description;
    private String dbAccount;
    private String dbPass;
    private String sshAccount;
    private String sshPass;
    private Integer nasId;
    private String nasName;
    private Integer portNumber;
    private Integer sshPort;
    private String passSudo;
}
