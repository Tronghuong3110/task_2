package com.newlife.Connect_multiple.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "sub_topic_on_server")
public class SubtopicServerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_subtopic")
    private Integer id;

    @Column(name = "subtopic")
    private String subTopic;

    @Column(name = "id_probe")
    private Integer idProbe;
}
