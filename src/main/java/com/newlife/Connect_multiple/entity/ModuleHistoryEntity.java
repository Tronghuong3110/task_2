package com.newlife.Connect_multiple.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Entity
@Data
@Getter
@Setter
@Table(name = "module_history")
public class ModuleHistoryEntity {

    @Id
    @Column(name = "id_module_history")
    private String idModuleHistory;

    @Column(name = "id_probe")
    private Integer idProbe;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Column(name = "title", columnDefinition = "text")
    private String title;

    @Column(name = "caption")
    private String caption;

    @Column(name = "arg")
    private String arg;

    @Column(name = "status")
    private String status;

    @Column(name = "at_time")
    private Timestamp atTime;

    @Column(name = "module_name", columnDefinition = "nvarchar(255)")
    private String moduleName;

    @Column(name = "note", columnDefinition = "text")
    private String note;

    @Column(name = "id_probe_module")
    private Integer idProbeModule;

    @Column(name = "probe_name")
    private String probeName;

    @Column(name = "ack")
    private Integer ack;
}
