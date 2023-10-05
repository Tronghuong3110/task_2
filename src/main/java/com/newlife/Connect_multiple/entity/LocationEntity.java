package com.newlife.Connect_multiple.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.*;

@Entity
@Data
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

    @OneToMany(mappedBy = "locationEntity", fetch = FetchType.EAGER)
    List<AreaEntity> areaEntityList;
}
