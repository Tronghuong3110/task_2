package com.newlife.Connect_multiple.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
public class ScheduleRestore {
    private Optional<String> scheduleRestore; // thời gian đặt lịch
    private Integer idServerRestore; // id database server thực hiện restore
    private LocalDateTime scheduleRestoreTime; // thời gian restore dạng localdatetime
    private Integer id_info_database_backup; // dùng để lấy thông tên database đã backup trên ftp server
    private Optional<String> ipServer;// ipServer dùng để biết xóa database trên server nào
}
