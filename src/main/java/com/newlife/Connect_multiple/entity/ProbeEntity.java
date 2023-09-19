package com.newlife.Connect_multiple.entity;

import lombok.Data;

import java.util.*;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Data
@Table(name = "probe")
public class ProbeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_probe")
    private Integer id;

    @Column(name = "location", columnDefinition = "nvarchar(255)")
    private String location;

    @Column(name = "area", columnDefinition = "nvarchar(255)")
    private String area;

    @Column(name = "name", columnDefinition = "nvarchar(255)")
    private String name;

    @Column(name = "pub_topic", columnDefinition = "text")
    private String pubTopic;

    @Column(name = "status")
    private String status; // connected broker

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "connect_at")
    private Date connectAt;

    @Column(name = "number_stoped_module")
    private Integer numberStopedModule;

    @Column(name = "number_running_module")
    private Integer numberRunningModule;

    @Column(name = "number_pending_module")
    private Integer numberPendingModule;

    @Column(name = "number_failed_module")
    private Integer numberFailedModule;

    @Column(name = "olt")
    private String olt;

    @Column(name = "vlan")
    private Integer vlan;

    @Column(name = "description", columnDefinition = "ntext")
    private String description;


    @Column(name = "client_id")
    private String clientId;

    @Column(name = "total_module")
    private Integer totalModule;

    @Column(name = "create_at")
    private Date createAt;

    @Column(name = "deleted")
    private Integer deleted;

    @OneToOne
    @JoinColumn(name = "id_probe_option")
    private ProbeOptionEntity probeOptionEntity;

    @OneToMany(mappedBy = "probeEntity")
    List<ProbeHistoryEntity> probeHistoryEntityList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPubTopic() {
        return pubTopic;
    }

    public void setPubTopic(String pubTopic) {
        this.pubTopic = pubTopic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Date getConnectAt() {
        return connectAt;
    }

    public void setConnectAt(Date connectAt) {
        this.connectAt = connectAt;
    }

    public Integer getNumberStopedModule() {
        return numberStopedModule;
    }

    public void setNumberStopedModule(Integer numberStopedModule) {
        this.numberStopedModule = numberStopedModule;
    }

    public Integer getNumberRunningModule() {
        return numberRunningModule;
    }

    public void setNumberRunningModule(Integer numberRunningModule) {
        this.numberRunningModule = numberRunningModule;
    }

    public Integer getNumberPendingModule() {
        return numberPendingModule;
    }

    public void setNumberPendingModule(Integer numberPendingModule) {
        this.numberPendingModule = numberPendingModule;
    }

    public Integer getNumberFailedModule() {
        return numberFailedModule;
    }

    public void setNumberFailedModule(Integer numberFailedModule) {
        this.numberFailedModule = numberFailedModule;
    }

    public String getOlt() {
        return olt;
    }

    public void setOlt(String olt) {
        this.olt = olt;
    }

    public Integer getVlan() {
        return vlan;
    }

    public void setVlan(Integer vlan) {
        this.vlan = vlan;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProbeOptionEntity getProbeOptionEntity() {
        return probeOptionEntity;
    }

    public void setProbeOptionEntity(ProbeOptionEntity probeOptionEntity) {
        this.probeOptionEntity = probeOptionEntity;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Integer getTotalModule() {
        return totalModule;
    }

    public void setTotalModule(Integer totalModule) {
        this.totalModule = totalModule;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
