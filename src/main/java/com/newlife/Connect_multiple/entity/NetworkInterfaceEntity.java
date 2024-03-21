package com.newlife.Connect_multiple.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "network_interface")
public class NetworkInterfaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "interface_name")
    private String interfaceName;
    @Column(name = "monitor")
    private Integer monitor;
    @Column(name = "id_probe")
    private Integer idProbe;
    @Column(name = "status")
    private Integer status;
    @Column(name = "description")
    private String description;
}
