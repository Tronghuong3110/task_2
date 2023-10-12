package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.dto.ProbeHistoryDto;
import com.newlife.Connect_multiple.service.IProbeHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class DashBoardController {

    @Autowired
    private IProbeHistoryService probeHistoryService;

    // test man dashboard
    // lấy ra probe history
    @GetMapping ("/dashboard/probe/history")
    public List<ProbeHistoryDto> getProbeHistory(@RequestParam("num") Integer n) {
        return probeHistoryService.getLastNRecord(n);
    }
}
