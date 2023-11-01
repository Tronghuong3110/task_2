package com.newlife.Connect_multiple.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Data
@Getter
@Setter
@Table(name = "server")
public class ServerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_server")
    private Integer id;

    @Column(name = "name", columnDefinition = "nvarchar(255)")
    private String name;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "pub_topic", columnDefinition = "text")
    private String pubTopic;

    @Column(name = "connect_at")
    private Date connectAt;

    @Column(name = "server_id_connect")
    private String serverIdConnect;

    @OneToOne
    @JoinColumn(name = "id_probe_option")
    private ProbeOptionEntity probeOptionEntity;
}
