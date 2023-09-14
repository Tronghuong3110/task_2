package com.newlife.Connect_multiple.entity;

import javax.persistence.*;

@Entity
@Table(name = "sub_topic_on_server")
public class SubtopicServerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_subtopic")
    private Integer id;

    @Column(name = "subtopic")
    private String subTopic;
}
