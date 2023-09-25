package com.newlife.Connect_multiple.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Data
@Table(name = "probe_history")
public class ProbeHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_probe_history")
    private Integer id;

    @Column(name = "action")
    private String action;

    @Column(name = "at_time")
    private Date atTime;

    @Column(name = "probe_name", columnDefinition = "nvarchar(255)")
    private String probeName;

    @ManyToOne
    @JoinColumn(name = "id_probe")
    private ProbeEntity probeEntity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Date getAtTime() {
        return atTime;
    }

    public void setAtTime(Date atTime) {
        this.atTime = atTime;
    }

    public String getProbeName() {
        return probeName;
    }

    public void setProbeName(String probeName) {
        this.probeName = probeName;
    }

    public ProbeEntity getProbeEntity() {
        return probeEntity;
    }

    public void setProbeEntity(ProbeEntity probeEntity) {
        this.probeEntity = probeEntity;
    }
}
