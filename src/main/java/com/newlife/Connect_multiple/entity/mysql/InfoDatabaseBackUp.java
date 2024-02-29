package com.newlife.Connect_multiple.entity.mysql;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "info_database_backup")
public class InfoDatabaseBackUp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name_database_ftp_server")
    private String databaseName;
    @Column(name = "time_backup")
    private String timeBackup;
    @Column(name = "restore_status")
    private String restoreStatus;
    @Column(name = "restore_process")
    private Double restoreProcess;
    @Column(name ="time_end_restore")
    private String timeEndRestore;
    @Column(name = "time_start_restore")
    private String timeStartRestore;
}
