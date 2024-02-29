package com.newlife.Connect_multiple.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "nas")
public class NasEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "username")
    private String username;
    @Column(name = "nas_ip")
    private String ip;
    @Column(name = "port")
    private String port;
    @Column(name = "description")
    private String description;
    @Column(name = "path")
    private String path;
    @Column(name = "password")
    private String password;
    @Column(name = "nas_name")
    private String nasName;
}
