package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.dto.Ids;
import com.newlife.Connect_multiple.dto.ModuleHistoryDto;
import com.newlife.Connect_multiple.entity.ModuleHistoryEntity;
import com.newlife.Connect_multiple.service.IModuleHistoryService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
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
    public CompletableFuture<JSONObject> deleteModuleHistory (@RequestBody Ids ids) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject mess = moduleHistoryService.deleteModuleHistory(ids.getIdsStr());
            return mess;
        }, executorService);
    }

    @GetMapping("/moduleHistories") // Tên module / tên probe / thời gian / đã được ACK hay chưa
    public CompletableFuture<List<ModuleHistoryDto> > findAllPage(@RequestParam("idProbeModule")Optional<Integer> idProbeModule,
                                                                  @RequestParam("idProbe") Optional<Integer> idProbe,
                                                                  @RequestParam("timeStart") Optional<String> timeStart,
                                                                  @RequestParam("timeEnd") Optional<String> timeEnd,
                                                                  @RequestParam("ack") Optional<Integer> ack,
                                                                  @RequestParam("page") Optional<Integer> page,
                                                                  @RequestParam("content") Optional<String> content) {
        return CompletableFuture.supplyAsync(() -> {
            List<ModuleHistoryDto> listResult = moduleHistoryService.findAllModuleHistoryByCondition(idProbeModule.orElse(null), idProbe.orElse(null),
                    timeStart.orElse(null), timeEnd.orElse(null), ack.orElse(null), content.orElse(null), page.orElse(0));
                System.out.println("time start " + timeStart.orElse(null));
                System.out.println("time end " + timeEnd.orElse(null));
                System.out.println("ACK " + ack.orElse(null));
                System.out.println("Id probe " + idProbe.orElse(null));
            return listResult;
        }, executorService);
    }

    @PutMapping("/moduleHistory")
    public CompletableFuture<ResponseEntity<?>> updateAck(@RequestParam("idModuleHistory") String id) {
        return CompletableFuture.supplyAsync(() -> {
            Integer status = moduleHistoryService.updateAck(id);
            if(status == 1) return ResponseEntity.ok(status);
            return ResponseEntity.badRequest().body(status);
        }, executorService);
    }
}
