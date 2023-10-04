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

}
