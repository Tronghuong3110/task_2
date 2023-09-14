package com.newlife.Connect_multiple.entity;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "probe_module")
public class ProbeModuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_probe_module")
    private Integer id;

    @Column(name = "id_module")
    private Integer idModule;

    @Column(name = "id_probe")
    private Integer idProbe;

    @Column(name = "path")
    private String path;

    @Column(name = "caption")
    private String caption;

    @Column(name = "arg")
    private String arg;

    @Column(name = "command")
    private String command;

    @Column(name = "path_log")
    private String pathLog;

    @Column(name = "status")
    private Integer status;

    @Column(name = "expect")
    private Integer expect;

    @Column(name = "process_id")
    private String processId;

    @Column(name = "note", columnDefinition = "text")
    private String note;

    @OneToMany(mappedBy = "probeModuleEntity")
    private List<ModuleHistoryEntity> moduleHistoryEntityList;
}
