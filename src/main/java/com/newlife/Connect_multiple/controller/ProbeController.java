package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.dto.*;
import com.newlife.Connect_multiple.service.ILocationService;
import com.newlife.Connect_multiple.service.IProbeService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class ProbeController {

    @Autowired
    private IProbeService probeService;

    @Autowired
    private ILocationService locationService;
    private ExecutorService executorService = Executors.newFixedThreadPool(15);

    // lấy ra 1 probe (đã test thành công) (Han)
    @GetMapping("/probe")
    public CompletableFuture<ProbeDto> getOneProbe(@RequestParam("idProbe") Integer idProbe) {
        return CompletableFuture.supplyAsync(() -> {
            ProbeDto probeDto = probeService.findOneProbe(idProbe);
            return probeDto;
        }, executorService);
    }

    // thêm mới probe
    @PostMapping("/probe/import")
    public CompletableFuture<ResponseEntity<?>> createProbe(@RequestBody RequestData requestData) {
        return CompletableFuture.supplyAsync(() -> {
            ProbeDto response = probeService.saveProbe(requestData.getProbeDto(), requestData.getProbeOptionDto());
            return ResponseEntity.ok(response);
        }, executorService);
    }

    // lấy ra toàn bộ location
    @GetMapping("/locations")
    public CompletableFuture<List<LocationDto>> getListLocation() {
        return CompletableFuture.supplyAsync(() -> {
            List<LocationDto> listLocations = locationService.findAll();
            return listLocations;
        }, executorService);
    }

//    lấy ra toàn bộ probe + tìm kiếm theo các điều kiện
    @GetMapping("/probes")
    public CompletableFuture<List<ProbeDto>> searchprobe(@RequestParam("name") Optional<String> name,
                                       @RequestParam("location") Optional<String> location,
                                       @RequestParam("area") Optional<String> area,
                                       @RequestParam("vlan") Optional<String> vlan) {
        return CompletableFuture.supplyAsync(() -> {
            List<ProbeDto> response = probeService.findAllProbe(name.orElse(""), location.orElse(""),
                                                                area.orElse(""), vlan.orElse("") );
            return response;
        }, executorService);
    }

    // xóa 1 probe (di chuyển probe tới thùng rác) - Hướng
    @DeleteMapping("/probe")
    public CompletableFuture<JSONObject> deleteProbe(@RequestParam("id") Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject message = probeService.delete(id);
            return message;
        }, executorService);
    }

    // xóa 1 probe (xóa vĩnh viễn probe từ probe) - Hướng
    @DeleteMapping("/probe/remove")
    public CompletableFuture<JSONObject> deleteProbeFromTrash(@RequestParam("id") Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject messageDelete = probeService.deleteProbe(id);
            return messageDelete;
        }, executorService);
    }

    // cập nhật chỉnh sửa thông tin probe
    @PutMapping("/probe")
    public CompletableFuture<JSONObject> updateProbe(@RequestBody ProbeDto probeDto) {
        return CompletableFuture.supplyAsync(() -> {
//            System.out.println("ID probe" + probeDto.getId());
//            System.out.println("Status of controller " + probeDto.getStatus());
            JSONObject message = probeService.updateProbe(probeDto);
            return message;
        }, executorService);
    }

    //khôi phục probe từ thùng rác
    @PostMapping("/probe")
    public CompletableFuture<JSONObject> backUpProbe(@RequestParam("id") Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject message = probeService.backUpProbe(id);
            return message;
        }, executorService);
    }

    // thông tin probe để kết nối tới broker (username, password, topic được mã hóa)
    @GetMapping("/downloadFile/{probeId}")
    public CompletableFuture<ResponseEntity<?>> downloadFile(@PathVariable("probeId") Integer probeId) {
        return CompletableFuture.supplyAsync(() -> {
            InfoLogin info = probeService.downlodFile(probeId);
            return ResponseEntity.ok(info);
        }, executorService);
    }

    // Test man Dashboard (da test thanh cong)
    @GetMapping("/dashboard/probe")
    public CompletableFuture<Integer> countProbeByStatus(@RequestParam("status") String status) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Integer tmp = probeService.countProbeByStatus(status);
                return tmp;
            } catch (Exception e){
                e.printStackTrace();
                return -1;
            }
        }, executorService);
    }
}
