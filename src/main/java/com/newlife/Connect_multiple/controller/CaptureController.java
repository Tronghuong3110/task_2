package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.dto.InfoCaptureBackup;
import com.newlife.Connect_multiple.dto.InfoCaptureSetting;
import com.newlife.Connect_multiple.dto.InfoDatabaseBackup;
import com.newlife.Connect_multiple.dto.ScheduleRestore;
import com.newlife.Connect_multiple.service.IInfoCaptureSettingService;
import org.hibernate.cfg.SecondaryTableSecondPass;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class CaptureController {

    @Autowired
    private IInfoCaptureSettingService infoCaptureSettingService;
    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    private PriorityQueue<ScheduleRestore> requestRestoreQueue = null;

    @GetMapping("/captures")
    public CompletableFuture<ResponseEntity<?>> findAllCaptureSetting(@RequestParam("probeName")Optional<String> probeName, @RequestParam("province") Optional<String> province,
                                                                      @RequestParam("backupStatus") Optional<String> backupStatus, @RequestParam("monitorStatus")Integer monitorStatus)  {
        return CompletableFuture.supplyAsync(() -> {
            Boolean[] test;
            if(monitorStatus.equals(0)) { // tim kiem theo monitor
                test = new Boolean[]{false};
            }
            else if(monitorStatus.equals(1)) { // tim kiem theo no monitor
                test = new Boolean[]{true};
            }
            else { // khong tim kiem theo gia tri nao
                test = new Boolean[]{true, false};
            }
            List<InfoCaptureSetting> listResponse = infoCaptureSettingService.findAll(probeName.orElse(""), province.orElse(""),
                    test, backupStatus.orElse(""));

            if(listResponse == null) {
                return ResponseEntity.badRequest().body("Find all database error");
            }
            return ResponseEntity.ok(listResponse);
        }, executorService);
    }

    @PostMapping("/capture/backup")
    public CompletableFuture<ResponseEntity<?>> backupDatabase(@RequestParam("idServer") Integer idServer, @RequestParam("databaseName") String databaseName, @RequestParam("idInfo")Integer idInfo,
                                                               @RequestBody ScheduleRestore scheduleRestore, @RequestParam("restore")String restore, @RequestParam("delete") Integer delete) {
        // cần biết server nào để thực hiện ssh đến
        // cần cấu hình sẵn thông tin ftp server trên mỗi clickhouse server
        // cần biết thông tin của database nào(tên database) để thực hiêện backup data
        // cần thêm thông tin pass của server để chạy quyền sudo
        return CompletableFuture.supplyAsync(() -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String timeScheduleStr = scheduleRestore.getScheduleRestore().orElse(null);
            String ipServer = scheduleRestore.getIpServer().orElse(null);
            JSONObject response = infoCaptureSettingService.backUpDatabase(idServer, databaseName, idInfo);
            if(response.get("code").equals(0)) {
                return ResponseEntity.status(500).body(response);
            }
            // TH backup thành công và yêu cầu restore lại ngay sau khi backup
            Integer id_info_database_backup = Integer.parseInt(response.get("id_info_database_backup").toString());
            if(restore.equals("true")) {
                System.out.println("yeu cau resstore ngay " + restore);
                infoCaptureSettingService.restoreDatabase(scheduleRestore.getIdServerRestore(), id_info_database_backup);
//                return ResponseEntity.ok("Success");
            }
            // TH backup thanh công ==> lên lịch cho việc restore
            if(timeScheduleStr != null) {
                Long timeSchedule = ZonedDateTime.of(LocalDateTime.parse(timeScheduleStr, formatter), ZoneId.systemDefault()).toInstant().toEpochMilli();
                Long currentTime = System.currentTimeMillis();
                Long delay = timeSchedule - currentTime;
                if(delay > 0) { // TH backup xong và thực hiện đặt lịch để restore
                    scheduleRestore.setScheduleRestoreTime(LocalDateTime.parse(timeScheduleStr, formatter));
                    scheduleRestore.setId_info_database_backup(id_info_database_backup);
                    if(requestRestoreQueue == null) {
                        requestRestoreQueue = new PriorityQueue<>((r1, r2) -> r1.getScheduleRestoreTime().compareTo(r2.getScheduleRestoreTime()));
                    }
                    requestRestoreQueue.add(scheduleRestore);
                }
                else {
                    return ResponseEntity.badRequest().body("It is not possible to schedule a restore with a time smaller than the estimated backup time.");
                }
            }
            // TH backup xong va yeu cau xoa database tren server
            if(delete.equals(1)) {
                System.out.println("Yeu cau xoa database ngay " + delete);
                if(ipServer != null) {
                    JSONObject jsonObject = infoCaptureSettingService.deleteDatabase(ipServer, databaseName, idInfo);
                }
            }

            return ResponseEntity.ok(response);
        }, executorService);
    }

    @GetMapping("/info/database/restore") // lấy ra thông tin database khi đã backup thành công lên nas
    public CompletableFuture<ResponseEntity<?>> findAllInfoRestore(@RequestParam("databaseName")String databaseName, @RequestParam("idRestore") Optional<Integer>id) {
        return CompletableFuture.supplyAsync(() -> {
            List<InfoDatabaseBackup> listResponse = infoCaptureSettingService.findAllInfo(databaseName, id.orElse(null));
            if(listResponse == null) {
                return ResponseEntity.badRequest().body("Can not load list database name");
            }
            return ResponseEntity.ok(listResponse);
        }, executorService);
    }

    @PostMapping("/capture/restore")
    public CompletableFuture<ResponseEntity<?>> restoreDatabase(@RequestParam("idInfoDatabase")Integer idInfoDatabase, @RequestParam("idServer")Integer idServer) {
        return CompletableFuture.supplyAsync(() -> {
            infoCaptureSettingService.restoreDatabase(idServer, idInfoDatabase);
//            return ResponseEntity.ok(response);
            return null;
        }, executorService);
    }

    @GetMapping("/info/capture/backup")
    public CompletableFuture<ResponseEntity<?>> findOne(@RequestParam("idDatabaseServer") Integer idDatabaseServer, @RequestParam("idNas") Integer idNas) {
        return CompletableFuture.supplyAsync(() -> {
            InfoCaptureBackup infoCaptureBackup = infoCaptureSettingService.findOneInfo(idDatabaseServer, idNas);
            if(infoCaptureBackup == null) {
                return ResponseEntity.badRequest().body("Find one database server and nas error");
            }
            return ResponseEntity.ok(infoCaptureBackup);
        }, executorService);
    }

    @DeleteMapping("/delete/database")
    public CompletableFuture<ResponseEntity<?>> deleteDatabase(@RequestParam("ipServer") String ipServer, @RequestParam("databaseName") String databaseName, @RequestParam("idInfo") Integer idInfo) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject response = infoCaptureSettingService.deleteDatabase(ipServer, databaseName, idInfo);
            if(response.get("code").equals(0)) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        }, executorService);
    }

    @GetMapping("/server/list/database")
    public CompletableFuture<?> findAllDatabaseOfServer(@RequestParam("idServer") Integer idServer) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> listDatabase = infoCaptureSettingService.findAllDatabaseNameOfServer(idServer);
            if(listDatabase == null) {
                return ResponseEntity.badRequest().body("Can not found list database of server");
            }
            return ResponseEntity.ok(listDatabase);
        }, executorService);
    }

    // thực hiện restore theo lịch đã được đặt trước
    public void solveRestoreTime() {
//        CompletableFuture.runAsync(() -> {
            while (true) {
//                ExecutorService executorService1 = Executors.newFixedThreadPool(requestRestoreQueue.size() + 1);
                try {
//                    System.out.println("Test schedule");
//                   if(this.requestRestoreQueue != null) {
//                       System.out.println("So luong request trong hang doi den thoi gian thuc hien " + this.requestRestoreQueue.size());
                   Thread.sleep(2000);
//                   }
                    while (this.requestRestoreQueue != null && !this.requestRestoreQueue.isEmpty()) {
                        ScheduleRestore schedule = this.requestRestoreQueue.poll();
                        Long timeSchedule = ZonedDateTime.of(schedule.getScheduleRestoreTime(), ZoneId.systemDefault()).toInstant().toEpochMilli();
                        Long currentTime = System.currentTimeMillis();
                        System.out.println("Time " + (timeSchedule - currentTime));
                        Long delay = Math.abs(timeSchedule - currentTime);
                        if(schedule != null && schedule.getScheduleRestoreTime().equals(LocalDateTime.now()) || (0 <= delay && delay <= 10000)) {
                            CompletableFuture.runAsync(() -> {
                                System.out.println("Start restore " + LocalDateTime.now());
                                infoCaptureSettingService.restoreDatabase(schedule.getIdServerRestore(), schedule.getId_info_database_backup());
                            }, executorService);
                        }
                        else {
                            this.requestRestoreQueue.add(schedule);
                        }
                        Thread.sleep(5000);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
//        }, executorService);
    }
}
