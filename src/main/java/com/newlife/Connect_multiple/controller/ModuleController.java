package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.dto.ModuleDto;
import com.newlife.Connect_multiple.service.IModuleService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class ModuleController {

    @Autowired
    private IModuleService moduleService;

    private ExecutorService executorService = Executors.newFixedThreadPool(6);

    // lấy ra toàn bộ module từ bảng module + tìm kiếm theo name(Hướng)
    @GetMapping("/modules")
    public CompletableFuture<List<ModuleDto>> findAllModule(@RequestParam("name")Optional<String> name) {
        return CompletableFuture.supplyAsync(() -> {
            List<ModuleDto> listModules = moduleService.findAllModule(name.orElse(""));
            return listModules;
        }, executorService);
    }

    // Thêm mới 1 một module chung (đã test thành công) (Han)
    @PostMapping("/module/import")
    public CompletableFuture<JSONObject> createModule(@RequestBody ModuleDto moduleDto) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject mess = moduleService.saveModule(moduleDto);
            return mess;
        }, executorService);
    }

    // Xóa 1 module lớn (ok) *****(cần xem xét việc xóa các module-probe con hay không)***
    // (Han)
    @DeleteMapping("/module")
    public CompletableFuture<JSONObject> deleteModule(@RequestParam("id") Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject mess = moduleService.deleteModule(id);
            return mess;
        }, executorService);
    }

    // Update 1 module lớn (đã test thành công) (Hân)
    @PutMapping("/module")
    public CompletableFuture<JSONObject> updateModule(@RequestBody ModuleDto moduleDto) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject mess = moduleService.updateModule(moduleDto);
            return mess;
        }, executorService);
    }

    @GetMapping("/module")
    public ModuleDto getOneModule(@RequestParam("idModule") Integer idModule) {
        ModuleDto moduleDto = moduleService.findOneModule(idModule);
        return moduleDto;
    }

}
