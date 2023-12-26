package com.newlife.Connect_multiple.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "cpu")
public class PerformanceCpu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "id_probe")
    private Integer idProbe;
    @Column(name = "time")
    private String time;
    @Column(name = "unix_time")
    private String unixTime;
    @Column(name = "check")
    private Integer check;

    @Column(name = "cpu_1")
    private Double cpu1;

    @Column(name = "cpu_2")
    private Double cpu2;

    @Column(name = "cpu_3")
    private Double cpu3;

    @Column(name = "cpu_4")
    private Double cpu4;

    @Column(name = "cpu_5")
    private Double cpu5;

    @Column(name = "cpu_6")
    private Double cpu6;

    @Column(name = "cpu_7")
    private Double cpu7;

    @Column(name = "cpu_8")
    private Double cpu8;

    @Column(name = "cpu_9")
    private Double cpu9;

    @Column(name = "cpu_10")
    private Double cpu10;

    @Column(name = "cpu_11")
    private Double cpu11;

    @Column(name = "cpu_12")
    private Double cpu12;

    @Column(name = "cpu_13")
    private Double cpu13;

    @Column(name = "cpu_14")
    private Double cpu14;

    @Column(name = "cpu_15")
    private Double cpu15;

    @Column(name = "cpu_16")
    private Double cpu16;

    @Column(name = "cpu_17")
    private Double cpu17;

    @Column(name = "cpu_18")
    private Double cpu18;

    @Column(name = "cpu_19")
    private Double cpu19;

    @Column(name = "cpu_20")
    private Double cpu20;

    @Column(name = "cpu_21")
    private Double cpu21;

    @Column(name = "cpu_22")
    private Double cpu22;

    @Column(name = "cpu_23")
    private Double cpu23;

    @Column(name = "cpu_24")
    private Double cpu24;

    @Column(name = "cpu_25")
    private Double cpu25;

    @Column(name = "cpu_26")
    private Double cpu26;

    @Column(name = "cpu_27")
    private Double cpu27;

    @Column(name = "cpu_28")
    private Double cpu28;

    @Column(name = "cpu_29")
    private Double cpu29;

    @Column(name = "cpu_30")
    private Double cpu30;

    @Column(name = "cpu_31")
    private Double cpu31;

    @Column(name = "cpu_32")
    private Double cpu32;

    @Column(name = "cpu_33")
    private Double cpu33;

    @Column(name = "cpu_34")
    private Double cpu34;

    @Column(name = "cpu_35")
    private Double cpu35;

    @Column(name = "cpu_36")
    private Double cpu36;

    @Column(name = "cpu_37")
    private Double cpu37;

    @Column(name = "cpu_38")
    private Double cpu38;

    @Column(name = "cpu_39")
    private Double cpu39;

    @Column(name = "cpu_40")
    private Double cpu40;

    @Column(name = "cpu_41")
    private Double cpu41;

    @Column(name = "cpu_42")
    private Double cpu42;

    @Column(name = "cpu_43")
    private Double cpu43;

    @Column(name = "cpu_44")
    private Double cpu44;

    @Column(name = "cpu_45")
    private Double cpu45;

    @Column(name = "cpu_46")
    private Double cpu46;

    @Column(name = "cpu_47")
    private Double cpu47;

    @Column(name = "cpu_48")
    private Double cpu48;

    @Column(name = "cpu_49")
    private Double cpu49;

    @Column(name = "cpu_50")
    private Double cpu50;

    @Column(name = "cpu_51")
    private Double cpu51;

    @Column(name = "cpu_52")
    private Double cpu52;

    @Column(name = "cpu_53")
    private Double cpu53;

    @Column(name = "cpu_54")
    private Double cpu54;

    @Column(name = "cpu_55")
    private Double cpu55;

    @Column(name = "cpu_56")
    private Double cpu56;

    @Column(name = "cpu_57")
    private Double cpu57;

    @Column(name = "cpu_58")
    private Double cpu58;

    @Column(name = "cpu_59")
    private Double cpu59;

    @Column(name = "cpu_60")
    private Double cpu60;

    @Column(name = "cpu_61")
    private Double cpu61;

    @Column(name = "cpu_62")
    private Double cpu62;

    @Column(name = "cpu_63")
    private Double cpu63;

    @Column(name = "cpu_64")
    private Double cpu64;

}
