package com.newlife.Connect_multiple.entity;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "server")
public class ServerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_server")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "pub_topic", columnDefinition = "text")
    private String pubTopic;

    @Column(name = "connect_at")
    private Date connectAt;

    @OneToOne
    @JoinColumn(name = "id_probe_option")
    private ProbeOptionEntity probeOptionEntity;
}
