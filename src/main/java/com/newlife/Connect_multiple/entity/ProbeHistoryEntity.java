package com.newlife.Connect_multiple.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Data
@Getter
@Setter
@Table(name = "probe_history")
public class ProbeHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_probe_history")
    private Integer id;

    @Column(name = "action")
    private String action;

    @Column(name = "at_time")
    private Timestamp atTime;

    @Column(name = "probe_name", columnDefinition = "nvarchar(255)")
    private String probeName;

}
