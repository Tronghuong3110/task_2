package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.service.IConfigService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class ConfigController {
    @Autowired
    private IConfigService configService;
    private ExecutorService executorService = Executors.newFixedThreadPool(3);

    @PostMapping("/config/server")
    public CompletableFuture<ResponseEntity<?>> configServer(@RequestParam("idServer")Integer idServer,
                                                             @RequestParam("idNas")Integer idNas) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject response = configService.configNasAndDb(idServer, idNas);
            if(response.get("code").equals(0)) {
                return ResponseEntity.status(500).body(response);
            }
            return ResponseEntity.ok(response);
        }, executorService);
    }
}
