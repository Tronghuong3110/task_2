package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.dto.LocationDto;
import com.newlife.Connect_multiple.dto.ProbeDto;
import com.newlife.Connect_multiple.dto.RequestData;
import com.newlife.Connect_multiple.service.ILocationService;
import com.newlife.Connect_multiple.service.IProbeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class ProbeController {

    @Autowired
    private IProbeService probeService;

    @Autowired
    private ILocationService locationService;

    @PostMapping("/probe/import")
    public ResponseEntity<?> createProbe(@RequestBody RequestData requestData) {
        ProbeDto response = probeService.saveProbe(requestData.getProbeDto(), requestData.getProbeOptionDto());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/locations")
    public List<LocationDto> getListLocation() {
        List<LocationDto> listLocations = locationService.findAll();
        return listLocations;
    }

//    find all probe have pagination
    @GetMapping("/probes")
    public List<ProbeDto> searchprobe(@RequestParam("name") Optional<String> name,
                                       @RequestParam("location") Optional<String> location,
                                       @RequestParam("area") Optional<String> area,
                                       @RequestParam("vlan") Optional<String> vlan,
                                       @RequestParam("sortBy") Optional<String> sortBy,
                                       @RequestParam("page") Optional<Integer> page) {
        List<ProbeDto> response = probeService.findAllProbe(name.orElse(""), location.orElse(""),
                                                            area.orElse(""), vlan.orElse(""),
                                                            sortBy.orElse("id_probe"), page.orElse(0));
        return response;
    }

    @DeleteMapping("/probe")
    public String deleteProbe(@RequestParam("id") Integer id) {
        String message = probeService.delete(id);
        return message;
    }

    @PutMapping("/probe")
    public String updateProbe(@RequestBody ProbeDto probeDto) {
        String message = probeService.updateProbe(probeDto);
        return message;
    }

    @PostMapping("/probe")
    public String backUpProbe(@RequestParam("id") Integer id) {
        String message = probeService.backUpProbe(id);
        return message;
    }
    // Han
    @GetMapping("dashboard/probe")
    public Integer countProbeByStatus(@RequestParam("status") String status) {
        try {
            Integer tmp = probeService.countProbeByStatus(status);
            return tmp;
        } catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }
}
