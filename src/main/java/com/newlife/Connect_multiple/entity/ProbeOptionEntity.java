package com.newlife.Connect_multiple.entity;

import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.persistence.*;

@Entity
@Table(name = "probe_option")
public class ProbeOptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_probe_option")
    private Integer id;

    @Column(name = "keep_alive")
    private Integer keepAlive;

    @Column(name = "clean_session")
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(Integer keepAlive) {
        this.keepAlive = keepAlive;
    }

    public Boolean getCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(Boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public Integer getConnectionTimeOut() {
        return connectionTimeOut;
    }

    public void setConnectionTimeOut(Integer connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ServerEntity getServerEntity() {
        return serverEntity;
    }

    public void setServerEntity(ServerEntity serverEntity) {
        this.serverEntity = serverEntity;
    }

    public ProbeEntity getProbeEntity() {
        return probeEntity;
    }

    public void setProbeEntity(ProbeEntity probeEntity) {
        this.probeEntity = probeEntity;
    }
}
