package com.newlife.Connect_multiple.entity;

import java.util.*;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "probe")
public class ProbeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_probe")
    private Integer id;

    @Column(name = "location")
    private String location;

    @Column(name = "area")
    private String area;

    @Column(name = "name")
    private String name;

    @Column(name = "pub_topic", columnDefinition = "text")
    private String pubTopic;

    @Column(name = "status")
    private String status;

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

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @OneToOne
    @JoinColumn(name = "id_probe_option")
    private ProbeOptionEntity probeOptionEntity;

    @OneToMany(mappedBy = "probeEntity")
    List<ProbeHistory> probeHistoryList;
}
