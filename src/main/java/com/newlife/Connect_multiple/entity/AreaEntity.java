package com.newlife.Connect_multiple.entity;

import javax.persistence.*;

@Entity
@Table(name = "area")
public class AreaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_area")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "area_code")
    private String areaCode;

    @ManyToOne
    @JoinColumn(name = "id_location")
    private LocationEntity locationEntity;
}
