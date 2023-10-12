package com.newlife.Connect_multiple.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.*;

@Entity
@Data
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
    private String status;

    @Column(name = "expect_status")
    private Integer expectStatus;

    @Column(name = "process_id")
    private String processId;

    @Column(name = "note", columnDefinition = "text")
    private String note;

    @Column(name = "module_name", columnDefinition = "nvarchar(255)")
    private String moduleName;

    @Column(name = "error_per_week")
    private Integer errorPerWeek;

    @Column(name = "process_status")
    private Integer processStatus;

    @Column(name = "process_name")
    private String processName;

}
