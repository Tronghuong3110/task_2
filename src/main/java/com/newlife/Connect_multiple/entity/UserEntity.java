package com.newlife.Connect_multiple.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "password")
    private String password;

    @Column(name = "username")
    private String username;
}
