package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.service.IModuleHistoryService;
import com.newlife.Connect_multiple.service.IModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1")
public class ModuleHistoryController {
    @Autowired
    private IModuleHistoryService moduleHistoryService;


    @DeleteMapping("/module/history")  // HAN xóa 1 lịch sử module (đã test thành công)
    public String deleteModuleHistory (@RequestParam("id") String idModuleHistory) {
        String mess = moduleHistoryService.deleteModuleHistory(idModuleHistory);
        return mess;
    }
}
