package com.newlife.Connect_multiple.entity.mongodb;

import lombok.Data;

@Data
public class InfoDatabaseConnect {
    private String server;
    private Integer port;
    private String database_name;
    private String username;
    private String password;
    private String protocol;
}
