package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.service.IModuleHistoryService;
import com.newlife.Connect_multiple.service.IModuleService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1")
public class ModuleHistoryController {
    @Autowired
    private IModuleHistoryService moduleHistoryService;
    private ExecutorService executorService = Executors.newFixedThreadPool(6);

    @DeleteMapping("/module/history")  // HAN xóa 1 lịch sử module (đã test thành công)
    public CompletableFuture<JSONObject> deleteModuleHistory (@RequestParam("id") String idModuleHistory) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject mess = moduleHistoryService.deleteModuleHistory(idModuleHistory);
            return mess;
        }, executorService);
    }

    // Hướng
    @GetMapping("/module/history/count")
    public CompletableFuture<String> countModuleErrorPerWeek() {
        return CompletableFuture.supplyAsync(() -> {
            String countError = moduleHistoryService.solveEPW(1, "Failed");
            return countError;
        }, executorService);
    }
}
