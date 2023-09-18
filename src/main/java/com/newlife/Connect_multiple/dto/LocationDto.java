package com.newlife.Connect_multiple.dto;

import java.util.*;
public class LocationDto {

    private Integer id;
    private String name;
    private String code;
    private List<AreaDto> listArea;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<AreaDto> getListArea() {
        return listArea;
    }

    public void setListArea(List<AreaDto> listArea) {
        this.listArea = listArea;
    }
}
