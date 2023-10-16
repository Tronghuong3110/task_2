package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.dto.ProbeHistoryDto;
import com.newlife.Connect_multiple.service.IProbeHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class DashBoardController {

    @Autowired
    private IProbeHistoryService probeHistoryService;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    // test man dashboard
    // lấy ra probe history
    @GetMapping ("/dashboard/probe/history")
    public CompletableFuture<List<ProbeHistoryDto>> getProbeHistory(@RequestParam("num") Integer n) {
        return CompletableFuture.supplyAsync(() -> {
            return probeHistoryService.getLastNRecord(n);
        }, executorService);
    }
}
