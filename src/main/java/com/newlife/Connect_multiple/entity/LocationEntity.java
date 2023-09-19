package com.newlife.Connect_multiple.entity;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "location")
public class LocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_location")
    private Integer id;

    @Column(name = "name", columnDefinition = "nvarchar(255)")
    private String name;

    @Column(name = "location_code")
    private String locationCode;

    @OneToMany(mappedBy = "locationEntity")
    List<AreaEntity> areaEntityList;

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

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public List<AreaEntity> getAreaEntityList() {
        return areaEntityList;
    }

    public void setAreaEntityList(List<AreaEntity> areaEntityList) {
        this.areaEntityList = areaEntityList;
    }
}
