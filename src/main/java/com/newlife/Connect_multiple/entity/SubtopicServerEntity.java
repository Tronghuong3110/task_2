package com.newlife.Connect_multiple.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Data
@Getter
@Setter
@Table(name = "sub_topic_on_server")
public class SubtopicServerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_subtopic")
    private Integer id;

    @Column(name = "subtopic")
    private String subTopic;

    @Column(name = "id_probe")
    private Integer idProbe;
}
