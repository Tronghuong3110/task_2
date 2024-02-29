package com.newlife.Connect_multiple.entity.mongodb;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

//@Entity
@Data
public class Location {
//    @Id
    private String type;
    private Double[] coordinates;
}

