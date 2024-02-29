package com.newlife.Connect_multiple.entity.mongodb;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

//@Entity
@Data
public class CoorDinate {
//    @Id
    private Double coordinates_x;
    private Double coordinates_y;
}
