package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.entity.mongodb.DatabaseOfServer;
import com.newlife.Connect_multiple.entity.mysql.InfoVolumeDatabaseEntity;
import com.newlife.Connect_multiple.service.IInfoVolumeDatabase;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DatabaseOfServerController {

    @Autowired
    private IInfoVolumeDatabase infoVolumeDatabase;
    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    @GetMapping("/info/volumes")
    public CompletableFuture<ResponseEntity<?>> findAll(@RequestParam("ip")Optional<String> ipDb, @RequestParam("name") Optional<String> name,
                                                        @RequestParam("type")Optional<String> type) {
        return CompletableFuture.supplyAsync(() -> {
            List<InfoVolumeDatabaseEntity> listResponse = infoVolumeDatabase.findAll(name.orElse(""), ipDb.orElse(""), type.orElse(""));
            return ResponseEntity.ok(listResponse);
        }, executorService);
    }
}
