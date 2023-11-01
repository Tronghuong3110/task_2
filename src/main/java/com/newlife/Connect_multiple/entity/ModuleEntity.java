package com.newlife.Connect_multiple.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Data
@Getter
@Setter
@Table(name = "module")
public class ModuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_module")
    private Integer id;

    @Column(name = "name", columnDefinition = "nvarchar(255)")
    private String name;

    @Column(name = "caption")
    private String caption;

    @Column(name = "path_default")
    private String pathDefault;

    @Column(name = "create_at")
    private Date creatAt;

    @Column(name = "arg_default")
    private String argDefalt;

    @Column(name = "path_log_default")
    private String pathLogDefault;

    @Column(name = "note", columnDefinition = "text")
    private String note;

}
