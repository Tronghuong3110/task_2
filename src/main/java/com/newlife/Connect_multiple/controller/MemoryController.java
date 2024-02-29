package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.service.IMemoryService;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class MemoryController {
    @Autowired
    private IMemoryService memoryService;

    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    @GetMapping("/memories")
    public CompletableFuture<?> findAllMemory(@RequestParam("probeId") Integer probeId) {
        return CompletableFuture.supplyAsync(() -> {
            return memoryService.findAllMemory(probeId);
        }, executorService);
    }

    @GetMapping("/performance") // lấy ra 1 hàng gần nhất từ database
    public CompletableFuture<?> findAllPerformance(@RequestParam("probeId") Integer probeId, @RequestParam("number") Integer number) {
        return CompletableFuture.supplyAsync(() -> {
            return memoryService.findAllByTime(probeId, number);
        }, executorService);
    }

    // lấy ra 10 hàng gần nhất trong database

}
