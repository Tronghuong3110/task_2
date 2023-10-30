package com.newlife.Connect_multiple.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Data
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
    private Date atTime;

    @Column(name = "module_name", columnDefinition = "nvarchar(255)")
    private String moduleName;

    @Column(name = "note", columnDefinition = "text")
    private String note;

    @Column(name = "id_probe_module")
    private Integer idProbeModule;
}
