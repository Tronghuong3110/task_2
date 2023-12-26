package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.dto.ProbeModuleDto;
import com.newlife.Connect_multiple.dto.Ids;
import com.newlife.Connect_multiple.dto.TypeModuleDto;
import com.newlife.Connect_multiple.service.IProbeModuleService;
import com.newlife.Connect_multiple.service.ITypeModuleService;
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
    @Autowired
    private ITypeModuleService typeModuleService;
    private ExecutorService executorService = Executors.newFixedThreadPool(12);
    private Boolean checkProcessRun = false;
    private Boolean checkProcessStop = false;
    private Boolean checkProcessRestart = false;

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
    public CompletableFuture<ResponseEntity<?>> restartModule(@RequestBody Ids ids){
        return CompletableFuture.supplyAsync(() -> {
            checkProcessRestart = false;
            List<Integer> listIpModule = ids.getIds();
//            if(listIpModule.size() <= 0) {
//                return ResponseEntity.badRequest().body(0);
//            }
            for(Integer id : listIpModule) {
                try {
                    Object responseStop = probeModuleService.stopModule(id);
                    Object runModule = probeModuleService.runModule(id);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.badRequest().body(0);
                }
            }
            try {
                Thread.sleep(2000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            checkProcessRestart = true;
            return ResponseEntity.ok(1);
        }, executorService);
    }
    // chạy module (Hướng)
    @PostMapping("/probeModule/run")
    public CompletableFuture<ResponseEntity> runModule(@RequestBody Ids ids) {
        return CompletableFuture.supplyAsync(() -> {
            checkProcessRun = false;
            List<Integer> listIpModule = ids.getIds();
            for(Integer id : listIpModule) {
                try {
                    Object jsonObject = probeModuleService.runModule(id);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.badRequest().body(0);
                }
            }
            try {
                Thread.sleep(2000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            checkProcessRun = true;
            return ResponseEntity.ok(1);
        }, executorService);
    }
    // stop module (Hướng)
    @PostMapping("/probeModule/stop")
    public CompletableFuture<ResponseEntity<?>> stopModule(@RequestBody Ids ids) {
        return CompletableFuture.supplyAsync(() -> {
            checkProcessStop = false;
            List<Integer> listIpModule = ids.getIds();
//            if(listIpModule.size() <= 0) {
//                return ResponseEntity.badRequest().body(0);
//            }
            for(Integer id : listIpModule) {
                try {
                    System.out.println("id " + id);
                    Object jsonObject = probeModuleService.stopModule(id);
//                    Thread.sleep(3000);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(2000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            checkProcessStop = true;
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
    @PostMapping("/probeModule/action/run")
    public ResponseEntity<?> runModuleResponse(@RequestBody Ids ids) {
        if(ids.getIds().size() <= 0) return ResponseEntity.badRequest().body(0);
        return ResponseEntity.ok(1);
    }
    @PostMapping("/probeModule/action/stop")
    public ResponseEntity<?> stopModuleResponse(@RequestBody Ids ids) {
        if(ids.getIds().size() <= 0) return ResponseEntity.badRequest().body(0);
        return ResponseEntity.ok(1);
    }
    @PostMapping("/probeModule/action/restart")
    public ResponseEntity<?> restartModuleResponse(@RequestBody Ids ids) {
        if(ids.getIds().size() <= 0) return ResponseEntity.badRequest().body(0);
        return ResponseEntity.ok(1);
    }
    @GetMapping("/probeModule/check")
    public Integer checkProcessFinish() {
        System.out.println("Check stop " + checkProcessStop);
        System.out.println("Check run " + checkProcessRun);
        System.out.println("Check restart " + checkProcessRestart);
        if(checkProcessStop || checkProcessRestart || checkProcessRun) {
            checkProcessRun = false;
            checkProcessStop = false;
            checkProcessRestart = false;
            return 1;
        }
        return 0;
    }

    @GetMapping("/typeModule")
    public ResponseEntity<?> findAllTypeModule() {
        List<TypeModuleDto> moduleDtoList = typeModuleService.findAll();
        if(moduleDtoList == null) {
            return ResponseEntity.badRequest().body("Get all type module error");
        }
        return ResponseEntity.ok(moduleDtoList);
    }

    @GetMapping("/test123")
    public CompletableFuture<ResponseEntity<?>> findAllMmemories(@RequestParam("idProbe") Integer idProbe) {
        return CompletableFuture.supplyAsync(() -> {
            return ResponseEntity.ok(probeModuleService.findAllMemories(idProbe));
        }, executorService);
    }
}
