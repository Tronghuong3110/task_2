package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.dto.*;
import com.newlife.Connect_multiple.service.ILocationService;
import com.newlife.Connect_multiple.service.IProbeService;
import com.newlife.Connect_multiple.util.FileDownloadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class ProbeController {

    @Autowired
    private IProbeService probeService;

    @Autowired
    private ILocationService locationService;
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @PostMapping("/probe/import")
    public CompletableFuture<ResponseEntity<?>> createProbe(@RequestBody RequestData requestData) {
        return CompletableFuture.supplyAsync(() -> {
            ProbeDto response = probeService.saveProbe(requestData.getProbeDto(), requestData.getProbeOptionDto());
            return ResponseEntity.ok(response);
        }, executorService);
    }

    @GetMapping("/locations")
    public CompletableFuture<List<LocationDto>> getListLocation() {
        return CompletableFuture.supplyAsync(() -> {
            List<LocationDto> listLocations = locationService.findAll();
            return listLocations;
        }, executorService);
    }

//    find all probe have pagination
    @GetMapping("/probes")
    public CompletableFuture<List<ProbeDto>> searchprobe(@RequestParam("name") Optional<String> name,
                                       @RequestParam("location") Optional<String> location,
                                       @RequestParam("area") Optional<String> area,
                                       @RequestParam("vlan") Optional<String> vlan) {
        return CompletableFuture.supplyAsync(() -> {
            List<ProbeDto> response = probeService.findAllProbe(name.orElse(""), location.orElse(""),
                                                                area.orElse(""), vlan.orElse("") );
            return response;
        }, executorService);
    }

    @DeleteMapping("/probe")
    public CompletableFuture<String> deleteProbe(@RequestParam("id") Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            String message = probeService.delete(id);
            return message;
        }, executorService);
    }

    @PutMapping("/probe")
    public CompletableFuture<String> updateProbe(@RequestBody ProbeDto probeDto) {
        return CompletableFuture.supplyAsync(() -> {
            String message = probeService.updateProbe(probeDto);
            return message;
        }, executorService);
    }

    @PostMapping("/probe")
    public CompletableFuture<String> backUpProbe(@RequestParam("id") Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            String message = probeService.backUpProbe(id);
            return message;
        }, executorService);
    }

    @GetMapping("/downloadFile/{probeId}")
    public CompletableFuture<ResponseEntity<?>> downloadFile(@PathVariable("probeId") Integer probeId) {
        return CompletableFuture.supplyAsync(() -> {
            InfoLogin info = probeService.downlodFile(probeId);
            return ResponseEntity.ok(info);
        }, executorService);
    }
}
