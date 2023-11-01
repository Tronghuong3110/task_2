package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.dto.ProbeModuleDto;
import com.newlife.Connect_multiple.dto.ResquestModule;
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
    public CompletableFuture<JSONObject> deleteModuleProbe(@RequestParam("id") String id) {
        return CompletableFuture.supplyAsync(() -> {
            if(id.equals("")) {
                return new JSONObject();
            }
            List<String> ids = new ArrayList<>(Arrays.asList(id.split(" ")));
            JSONObject response = probeModuleService.delete(ids);
            return response;
        }, executorService);
    }

    // chạy lại module (Hướng)
    @PostMapping("/probeModule/restart")
    public CompletableFuture<ResponseEntity<?>> restartModule(@RequestBody ResquestModule resquestModule){
        return CompletableFuture.supplyAsync(() -> {
            List<Integer> listIpModule = resquestModule.getIds();
            if(listIpModule.size() <= 0) {
                return ResponseEntity.badRequest().body(0);
            }
            for(Integer id : listIpModule) {
                try {
                    Object responseStop = probeModuleService.stopModule(id);
                    Object runModule = probeModuleService.runModule(id);
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
    public CompletableFuture<ResponseEntity> runModule(@RequestBody ResquestModule resquestModule) {
        return CompletableFuture.supplyAsync(() -> {
//            if(idProbeModule.equals("")) {
//                return ResponseEntity.badRequest().body(0);
//            }
//            ArrayList<String> listIpModule = new ArrayList<>(Arrays.asList(idProbeModule.split(",")));
            List<Integer> listIpModule = resquestModule.getIds();
//            ArrayList<Integer> ids = new ArrayList<>();
            for(Integer id : listIpModule) {
                try {
                    Object jsonObject = probeModuleService.runModule(id);
        //                    Thread.sleep(3000);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.badRequest().body(0);
                }
            }
            return ResponseEntity.ok(1);
        }, executorService);
    }

    // stop module (Hướng)
    @PostMapping("/probeModule/stop")
    public CompletableFuture<ResponseEntity<?>> stopModule(@RequestBody ResquestModule resquestModule) {
        return CompletableFuture.supplyAsync(() -> {
            List<Integer> listIpModule = resquestModule.getIds();
            if(listIpModule.size() <= 0) {
                return ResponseEntity.badRequest().body(0);
            }
            for(Integer id : listIpModule) {
                try {
                    Object jsonObject = probeModuleService.stopModule(id);
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