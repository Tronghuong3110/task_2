package com.newlife.Connect_multiple.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Data
@Getter
@Setter
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
    @Column(name = "description", columnDefinition = "text")
    private String description;
    @Column(name = "client_id")
    private String clientId;
    @Column(name = "total_module")
    private Integer totalModule;
    @Column(name = "create_at")
    private Date createAt;
    @Column(name = "deleted")
    private Integer deleted;
    @Column(name = "token", columnDefinition = "text")
    private String token;

    @OneToOne
    @JoinColumn(name = "id_probe_option")
    private ProbeOptionEntity probeOptionEntity;
}
