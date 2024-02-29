package com.newlife.Connect_multiple.entity.mongodb;

import lombok.Data;

@Data
public class InfoRequestServer {
    private String ip;
    private Integer port_socket;
    private Integer port_ftp;
    private String ftp_username;
    private String ftp_password;
}
