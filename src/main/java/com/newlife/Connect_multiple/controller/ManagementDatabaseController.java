package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.dto.DatabaseServerDto;
import com.newlife.Connect_multiple.service.IDatabaseService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.image.CropImageFilter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1")
public class ManagementDatabaseController {

    @Autowired
    private IDatabaseService databaseService;
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @PostMapping("/database/import")
    public CompletableFuture<ResponseEntity<?>> saveDatabase(@RequestBody DatabaseServerDto databaseServerDto) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject response = databaseService.saveDatabaseServer(databaseServerDto);
            if(response.get("code").equals(1)) {
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.badRequest().body(response);
        }, executorService);
    }

    @GetMapping("/database/servers")
    public CompletableFuture<List<DatabaseServerDto>> findAll(@RequestParam("key")Optional<String> key) {
        return CompletableFuture.supplyAsync(()->{
            List<DatabaseServerDto> listDatabase = databaseService.findAllDatabaseServer(key.orElse(""));
            return listDatabase;
        }, executorService);
    }

    @GetMapping("/database/server")
    public CompletableFuture<DatabaseServerDto> findOneById(@RequestParam(("id")) Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            return databaseService.findOne(id);
        }, executorService);
    }

    @DeleteMapping("/database/server")
    public CompletableFuture<ResponseEntity<?>> deleteDatabase(@RequestParam("id") Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject response = databaseService.deleteDatabaseServer(id);
            if(response.get("code").equals(1)) {
                return ResponseEntity.ok("Delete database server with id = " + id + " success");
            }
            return ResponseEntity.badRequest().body("Delete database server fail");
        }, executorService);
    }

    @PutMapping("/database/server")
    public CompletableFuture<ResponseEntity<?>> updateDatabase(@RequestBody DatabaseServerDto databaseServerDto) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject response = databaseService.update(databaseServerDto);
            if(response.get("code").equals(1)) {
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.badRequest().body(response);
        }, executorService);
    }
}
