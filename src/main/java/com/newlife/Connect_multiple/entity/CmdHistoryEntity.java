package com.newlife.Connect_multiple.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Data
@Table(name = "cmd_history")
public class CmdHistoryEntity {

    @Id
    @Column(name = "id_cmd_history")
    private String id;

    @Column(name = "id_probe_module")
    private Integer idProbeModule;

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

    @Column(name = "status")
    private Integer status;

    @Column(name = "message", columnDefinition = "text")
    private String message;

    @Column(name = "retry_times")
    private Integer retryTimes;

    @Column(name = "at_time")
    private Date atTime;

    @Column(name = "modifiledate")
    private Date modifiledate;

}
