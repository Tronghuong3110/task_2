package com.newlife.Connect_multiple.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "database_server")
public class DatabaseServerMysql {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "ip_server")
    private String ipServer;
    @Column(name = "server_name")
    private String serverName;
    @Column(name = "type")
    private String type;
    @Column(name = "description")
    private String description;
    @Column(name = "db_account")
    private String dbAccount;
    @Column(name = "db_pass")
    private String dbPass;
    @Column(name = "ssh_account")
    private String sshAccount;
    @Column(name = "ssh_pass")
    private String sshPass;
    @Column(name = "nass_id")
    private Integer nasId;
    @Column(name = "nas_name")
    private String nasName;
    @Column(name = "port")
    private Integer portNumber;
    @Column(name = "ssh_port")
    private Integer sshPort;
    @Column(name = "pass_sudo")
    private String passSudo;
}
