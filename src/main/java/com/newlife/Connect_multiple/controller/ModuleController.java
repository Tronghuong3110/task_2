package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.dto.ModuleDto;
import com.newlife.Connect_multiple.dto.ProbeModuleDto;
import com.newlife.Connect_multiple.service.IModuleService;
import com.newlife.Connect_multiple.service.IProbeModuleService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class ModuleController {

    @Autowired
    private IModuleService moduleService;
    @Autowired
    private IProbeModuleService probeModuleService;

    private ExecutorService executorService = Executors.newFixedThreadPool(6);

    @GetMapping("/modules")
    public CompletableFuture<List<ModuleDto>> findAllModule(@RequestParam("name")Optional<String> name) {
        return CompletableFuture.supplyAsync(() -> {
            List<ModuleDto> listModules = moduleService.findAllModule(name.orElse(""));
            return listModules;
        }, executorService);
    }

    // lấy ra toàn bộ module theo probe
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

    @PostMapping("/probeModule/run")
    public CompletableFuture<ResponseEntity<?>> runModule(@RequestParam("idProbeModule") Optional<Integer> idProbeModule) {
        return CompletableFuture.supplyAsync(() -> {
            Object jsonObject = probeModuleService.runModule(idProbeModule.orElse(0));
            return ResponseEntity.ok(jsonObject);
        }, executorService);
    }

    @PostMapping("/probeModule/stop")
    public CompletableFuture<ResponseEntity<?>> stopModule(@RequestParam("idProbeModule") Optional<Integer> idProbeModule) {
        return CompletableFuture.supplyAsync(() -> {
            Object jsonObject = probeModuleService.stopModule(idProbeModule.orElse(0));
            return ResponseEntity.ok(jsonObject);
        }, executorService);
    }

    @PostMapping("/probeModule/restart")
    public CompletableFuture<ResponseEntity<?>> restartModule(@RequestParam("idProbeModule") Optional<Integer> idProbeModule){
        return CompletableFuture.supplyAsync(() -> {
            JSONObject responseMessage = new JSONObject();
            JSONObject responseStop = (JSONObject) probeModuleService.stopModule(idProbeModule.orElse(0));
            if(responseStop.get("status").equals("Stoped")) { // TH stop module thành công
                // Run module
                return ResponseEntity.ok(probeModuleService.runModule(idProbeModule.orElse(0)));
            }
            // TH stop module thất bại
            responseMessage.put("message", "Dừng module thất bại, không thể restart module");
            responseMessage.put("status", 4);
            return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
        }, executorService);
    }

    // Xóa 1 probe Module (đã test thành công) (Han)
    @DeleteMapping("/probeModule")
    public String deleteModuleProbe(@RequestParam("id") Integer id) {
        String message = probeModuleService.delete(id);
        return message;
    }


    // Thêm mới 1 probe Module (đã test thành công) (Han)
    @PostMapping("/probeModule/import")
    public String createModuleProbe(@RequestBody ProbeModuleDto probeModuleDto) {
        String mess = probeModuleService.saveProbeModule(probeModuleDto);
        return mess;
    }

    // Thêm mới 1 một module chung (đã test thành công) (Han)
    @PostMapping("/module/import")
    public String createModule(@RequestBody ModuleDto moduleDto) {
        String mess = moduleService.saveModule(moduleDto);
        return mess;
    }


    // Xóa 1 module lớn (ok) *****(cần xem xét việc xóa các module-probe con hay không)***
    // (Han)
    @DeleteMapping("/module")
    public String deleteModule(@RequestParam("id") Integer id) {
        String mess = moduleService.deleteModule(id);
        return mess;
    }

    // Update 1 module lớn (đã test thành công)
    @PutMapping("/module")
    public String updateModule(@RequestBody ModuleDto moduleDto) {
        String mess = moduleService.updateModule(moduleDto);
        return mess;
    }

    // Han
    // man dashboard (da test thanh cong)
    @GetMapping("/dashboard/module")
    public Integer countModuleByStatus(@RequestParam("status") String status) {
        Integer module = probeModuleService.countModuleByStatus(status);
        return module;
    }
}
