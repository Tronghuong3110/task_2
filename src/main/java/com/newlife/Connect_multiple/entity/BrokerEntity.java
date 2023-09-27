package com.newlife.Connect_multiple.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "broker")
public class BrokerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_broker")
    private Integer id;

    @Column(name = "url")
    private String url;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;
}
