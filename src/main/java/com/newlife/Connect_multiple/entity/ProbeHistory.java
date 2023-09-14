package com.newlife.Connect_multiple.entity;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "probe_history")
public class ProbeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_probe_history")
    private Integer id;

    @Column(name = "action")
    private String action;

    @Column(name = "at_time")
    private Date atTime;

    @ManyToOne
    @JoinColumn(name = "id_probe")
    private ProbeEntity probeEntity;
}
