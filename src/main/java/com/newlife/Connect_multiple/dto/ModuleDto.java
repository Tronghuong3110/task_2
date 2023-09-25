package com.newlife.Connect_multiple.dto;

import java.sql.Date;

public class ModuleDto {
    private Integer id;
    private String name;
    private String caption;
    private String pathDefault;
    private Date creatAt;
    private String argDefalt;
    private String pathLogDefault;
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
