package com.newlife.Connect_multiple.controller;

import com.mongodb.util.JSON;
import com.newlife.Connect_multiple.dto.NasDto;
import com.newlife.Connect_multiple.service.INasService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequestMapping("/api/v1")
@RestController
@CrossOrigin("*")
public class NasController {

    @Autowired
    private INasService nasService;
    private ExecutorService executorService = Executors.newFixedThreadPool(8);

    @PostMapping("/nas/import")
    public CompletableFuture<ResponseEntity<?>> saveNas(@RequestBody NasDto nasDto) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject response = nasService.saveNas(nasDto);
            if(response.containsKey("code") && response.get("code").equals(0)) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        }, executorService);
    }

    @GetMapping("/nases")
    public CompletableFuture<List<NasDto>> findAll() {
        return CompletableFuture.supplyAsync(() -> {
            List<NasDto> listResponse = nasService.findAllNas();
            return listResponse;
        }, executorService);
    }

    @PutMapping("/nas")
    public CompletableFuture<ResponseEntity<?>> updateNas(@RequestBody NasDto nasDto) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject response = nasService.updateNas(nasDto);
            if(response.get("code").equals(0)) {
                return ResponseEntity.status(500).body(response);
            }
            return ResponseEntity.ok(response);
        }, executorService);
    }

    @DeleteMapping("/nas")
    public CompletableFuture<ResponseEntity<?>> deleteNas(@RequestParam("idNas") Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject response = nasService.deleteNas(id);
            if(response.get("code").equals(0)) {
                return ResponseEntity.status(500).body(response);
            }
            return ResponseEntity.ok(response);
        }, executorService);
    }

    @GetMapping("/nas")
    public CompletableFuture<ResponseEntity<?>> findOne(@RequestParam("id") Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            NasDto nasDto = nasService.findOne(id);
            if(nasDto == null) {
                return ResponseEntity.status(404).body("Can not found nas server");
            }
            return ResponseEntity.ok(nasDto);
        }, executorService);
    }

    @PostMapping("/test/connect/ftp")
    public CompletableFuture<ResponseEntity<?>> testConnectFtp(@RequestParam("ip")String ip, @RequestParam("username")String username,
                                                               @RequestParam("pass")String pass, @RequestParam("port")Integer port) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject response = nasService.testConnectFtp(ip, username, pass, port);
            if(response.get("code").equals(0)) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        }, executorService);
    }
}
