package com.example.demo1;

import lombok.Data;

//@Data
public class InfoProbe {
    private String broker;
    private String clientId;
    private String username;
    private String password;
    private Integer connectionTimeOut;
    private String cleanSession;
    private String subTopic;
    private String pubTopic;
    private Integer keepAlive;
    private Integer idProbe;

    public Integer getIdProbe() {
        return idProbe;
    }

    public void setIdProbe(Integer idProbe) {
        this.idProbe = idProbe;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getConnectionTimeOut() {
        return connectionTimeOut;
    }

    public void setConnectionTimeOut(Integer connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }

    public String getCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(String cleanSession) {
        this.cleanSession = cleanSession;
    }

    public String getSubTopic() {
        return subTopic;
    }

    public void setSubTopic(String subTopic) {
        this.subTopic = subTopic;
    }

    public String getPubTopic() {
        return pubTopic;
    }

    public void setPubTopic(String pubTopic) {
        this.pubTopic = pubTopic;
    }

    public Integer getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(Integer keepAlive) {
        this.keepAlive = keepAlive;
    }
}
