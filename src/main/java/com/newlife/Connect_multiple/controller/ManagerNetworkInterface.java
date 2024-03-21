package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.dto.NetworkInterfaceDto;
import com.newlife.Connect_multiple.service.INetworkInterfaceService;
import com.newlife.Connect_multiple.service.IProbeService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class ManagerNetworkInterface {
    @Autowired
    private INetworkInterfaceService networkInterfaceService;
    @Autowired
    private IProbeService probeService;
    private ExecutorService executorService = Executors.newFixedThreadPool(3);

    @PostMapping("/interface/synchronize")
    public CompletableFuture<ResponseEntity<?>> synchronize(@RequestParam("idProbe")Integer idProbe) {
        return CompletableFuture.supplyAsync(() -> {
            probeService.findAllNetworkInterface(idProbe, 0);
            return null;
        }, executorService);
    }

    @GetMapping("/interface/list")
    public CompletableFuture<ResponseEntity<?>> findAllByProbe(@RequestParam("idProbe") Integer idProbe) {
        return CompletableFuture.supplyAsync(() -> {
            List<NetworkInterfaceDto> listResponse = networkInterfaceService.findAllByProbe(idProbe);
            return ResponseEntity.ok(listResponse);
        }, executorService);
    }

    @PutMapping("/interface/update")
    public CompletableFuture<ResponseEntity<?>> updateInterface(@RequestBody NetworkInterfaceDto networkInterfaceDto) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject response = networkInterfaceService.update(networkInterfaceDto);
            if(response.get("code").equals(0)) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        }, executorService);
    }
}
