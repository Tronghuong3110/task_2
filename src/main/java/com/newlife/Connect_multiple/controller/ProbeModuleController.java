package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.dto.ProbeModuleDto;
import com.newlife.Connect_multiple.service.IProbeModuleService;
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
public class ProbeModuleController {

    @Autowired
    private IProbeModuleService probeModuleService;

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    // Thêm mới 1 probe Module (đã test thành công) (Han)
    @PostMapping("/probeModule/import")
    public CompletableFuture<JSONObject> createModuleProbe(@RequestBody ProbeModuleDto probeModuleDto) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject response = probeModuleService.saveProbeModule(probeModuleDto);
            return response;
        }, executorService);
    }

    // Cập nhật probeModule Hướng
    @PutMapping("/probe/module")
    public CompletableFuture<JSONObject> updateProbeModule(@RequestBody ProbeModuleDto probeModuleDto) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject message = probeModuleService.updateProbeModule(probeModuleDto);
            return message;
        }, executorService);
    }

    // Xóa 1 probe Module (đã test thành công) (Han)
    @DeleteMapping("/probeModule")
    public CompletableFuture<JSONObject> deleteModuleProbe(@RequestParam("id") Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject response = probeModuleService.delete(id);
            return response;
        }, executorService);
    }

    // chạy lại module (Hướng)
    @PostMapping("/probeModule/restart")
    public CompletableFuture<ResponseEntity<?>> restartModule(@RequestParam("idProbeModule") String idProbeModule){
        return CompletableFuture.supplyAsync(() -> {
            ArrayList<String> listIpModule = new ArrayList<>(Arrays.asList(idProbeModule.split(" ")));
            for(String id : listIpModule) {
                try {
                    Object responseStop = probeModuleService.stopModule(Integer.parseInt(id));
                    Object runModule = probeModuleService.runModule(Integer.parseInt(id));
//                    Thread.sleep(3000);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return ResponseEntity.ok(1);
        }, executorService);
    }

    // chạy module (Hướng)
    @PostMapping("/probeModule/run")
    public CompletableFuture<ResponseEntity> runModule(@RequestParam("idProbeModule") String idProbeModule) {
        return CompletableFuture.supplyAsync(() -> {
            ArrayList<String> listIpModule = new ArrayList<>(Arrays.asList(idProbeModule.split(" ")));
            for(String id : listIpModule) {
                try {
                    Object jsonObject = probeModuleService.runModule(Integer.parseInt(id));
//                    Thread.sleep(3000);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return ResponseEntity.ok(1);
        }, executorService);
    }

    // stop module (Hướng)
    @PostMapping("/probeModule/stop")
    public CompletableFuture<ResponseEntity<?>> stopModule(@RequestParam("idProbeModule") String idProbeModule) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("id " + idProbeModule);
            ArrayList<String> listIpModule = new ArrayList<>(Arrays.asList(idProbeModule.split(" ")));
            for(String id : listIpModule) {
                try {
                    Object jsonObject = probeModuleService.stopModule(Integer.parseInt(id));
//                    Thread.sleep(3000);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return ResponseEntity.ok(1);
        }, executorService);
    }

    // Lấy ra 1 probeModule theo id (hướng)
    @GetMapping("/probe/module")
    public CompletableFuture<ProbeModuleDto> findOneById(@RequestParam("idProbeModule") Integer idProbeModule) {
        return CompletableFuture.supplyAsync(() -> {
            ProbeModuleDto probeModuleDto = probeModuleService.findOneById(idProbeModule);
            return probeModuleDto;
        },executorService);
    }

    // lấy ra toàn bộ module theo probe (hân)
    @GetMapping("/probe/modules")
    public CompletableFuture<List<ProbeModuleDto>> findAllProbeModule(@RequestParam("name") Optional<String> name,
                                                                      @RequestParam("status") Optional<String> status,
                                                                      @RequestParam("idProbe") Integer idProbe) {
        return CompletableFuture.supplyAsync(() -> {
            List<ProbeModuleDto> listProbeModules = probeModuleService.findAllProbeModule(name.orElse(""),
                    status.orElse(""), idProbe);
            return listProbeModules;
        }, executorService);
    }

    // Han
    // man dashboard (da test thanh cong)
    @GetMapping("/dashboard/module")
    public CompletableFuture<Integer> countModuleByStatus(@RequestParam("status") String status) {
        return CompletableFuture.supplyAsync(() -> {
            Integer module = probeModuleService.countModuleByStatus(status);
            return module;
        }, executorService);
    }

    @GetMapping("/probeModule/run")
    public String runModuleResponse() {
        return "1";
    }
    @GetMapping("/probeModule/stop")
    public String stopModuleResponse() {
        return "1";
    }
    @GetMapping("/probeModule/restart")
    public String restartModuleResponse() {
        return "1";
    }
}
