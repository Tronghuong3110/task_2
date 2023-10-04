package com.newlife.Connect_multiple.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "probe_option")
public class ProbeOptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_probe_option")
    private Integer id;

    @Column(name = "keep_alive")
    private Integer keepAlive;

    @Column(name = "clean_session", columnDefinition = "varchar(25)")
    private Boolean cleanSession;

    @Column(name = "connextion_time_out")
    private Integer connectionTimeOut;

    @Column(name = "username")
    private String userName;

    @Column(name = "password")
    private String password;

    @OneToOne(mappedBy = "probeOptionEntity")
    private ServerEntity serverEntity;

    @OneToOne(mappedBy = "probeOptionEntity")
    private ProbeEntity probeEntity;

}
