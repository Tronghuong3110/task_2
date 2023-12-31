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

    @Column(name = "content", columnDefinition = "ntext")
    private String content;

    @Column(name = "title", columnDefinition = "ntext")
    private String title;

    @Column(name = "caption")
    private String caption;

    @Column(name = "arg")
    private String arg;

    @Column(name = "status")
    private Integer status;

    @Column(name = "at_time")
    private Date atTime;

    @Column(name = "module_name", columnDefinition = "nvarchar(255)")
    private String moduleName;

    @Column(name = "note", columnDefinition = "ntext")
    private String note;

    @ManyToOne
    @JoinColumn(name = "id_probe_module")
    private ProbeModuleEntity probeModuleEntity;


}
