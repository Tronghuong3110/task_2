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

    @Column(name = "name")
    private String name;

    @Column(name = "location_code")
    private String locationCode;

    @OneToMany(mappedBy = "locationEntity")
    List<AreaEntity> areaEntityList;
}
