package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.dto.ProbeModuleDto;
import com.newlife.Connect_multiple.service.IProbeModuleService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class ProbeModuleController {

    @Autowired
    private IProbeModuleService probeModuleService;

    private ExecutorService executorService = Executors.newFixedThreadPool(7);

    // Thêm mới 1 probe Module (đã test thành công) (Han)
    @PostMapping("/probeModule/import")
    public String createModuleProbe(@RequestBody ProbeModuleDto probeModuleDto) {
        String mess = probeModuleService.saveProbeModule(probeModuleDto);
        return mess;
    }

    // Cập nhật probeModule Hướng
    @PutMapping("/probe/module")
    public CompletableFuture<String> updateProbeModule(@RequestBody ProbeModuleDto probeModuleDto) {
        return CompletableFuture.supplyAsync(() -> {
            String message = probeModuleService.updateProbeModule(probeModuleDto);
            return message;
        }, executorService);
    }

    // Xóa 1 probe Module (đã test thành công) (Han)
    @DeleteMapping("/probeModule")
    public String deleteModuleProbe(@RequestParam("id") Integer id) {
        String message = probeModuleService.delete(id);
        return message;
    }

    // chạy lại module (Hướng)
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


    // chạy module (Hướng)
    @PostMapping("/probeModule/run")
    public CompletableFuture<ResponseEntity<?>> runModule(@RequestParam("idProbeModule") Optional<Integer> idProbeModule) {
        return CompletableFuture.supplyAsync(() -> {
            Object jsonObject = probeModuleService.runModule(idProbeModule.orElse(0));
            return ResponseEntity.ok(jsonObject);
        }, executorService);
    }

    // stop module (Hướng)
    @PostMapping("/probeModule/stop")
    public CompletableFuture<ResponseEntity<?>> stopModule(@RequestParam("idProbeModule") Optional<Integer> idProbeModule) {
        return CompletableFuture.supplyAsync(() -> {
            Object jsonObject = probeModuleService.stopModule(idProbeModule.orElse(0));
            return ResponseEntity.ok(jsonObject);
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
}
