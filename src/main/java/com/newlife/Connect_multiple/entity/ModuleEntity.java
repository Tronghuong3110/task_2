package com.newlife.Connect_multiple.entity;

import javax.persistence.*;
import java.sql.Date;

@Entity
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

    @Column(name = "note", columnDefinition = "ntext")
    private String note;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getPathDefault() {
        return pathDefault;
    }

    public void setPathDefault(String pathDefault) {
        this.pathDefault = pathDefault;
    }

    public Date getCreatAt() {
        return creatAt;
    }

    public void setCreatAt(Date creatAt) {
        this.creatAt = creatAt;
    }

    public String getArgDefalt() {
        return argDefalt;
    }

    public void setArgDefalt(String argDefalt) {
        this.argDefalt = argDefalt;
    }

    public String getPathLogDefault() {
        return pathLogDefault;
    }

    public void setPathLogDefault(String pathLogDefault) {
        this.pathLogDefault = pathLogDefault;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
