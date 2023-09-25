package com.newlife.Connect_multiple.dto;

import lombok.Data;

import java.sql.Date;

@Data
public class ProbeDto {
    private Integer id;
    private String location;
    private String area; //
    private String name;
    private String status; // đại diện cho probe còn kết nối tới broker không
    private String ipAddress;
    private Date connectAt;
    private Integer numberStopedModule;
    private Integer numberRunningModule;
    private Integer numberPendingModule;
    private Integer numberFailedModule;
    private String olt;
    private Integer vlan;
    private String description;
    private Integer totalModule;
    private String message;

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

    public Integer getTotalModule() {
        return totalModule;
    }

    public void setTotalModule(Integer totalModule) {
        this.totalModule = totalModule;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
