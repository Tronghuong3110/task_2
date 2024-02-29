package com.newlife.Connect_multiple.entity.mongodb;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

//@Entity
@Data
public class NormalizedColumns {
//    @Id
    private String id;
    private String bras;
    private String department;
    private String district;
    private String name;
    private String province;
    private String status;
    private String vlan;
    private String start_monitor;
    private String stop_monitor;
}
