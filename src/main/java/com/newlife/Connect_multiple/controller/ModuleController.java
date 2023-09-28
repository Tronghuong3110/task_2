package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.dto.ModuleDto;
import com.newlife.Connect_multiple.dto.ProbeModuleDto;
import com.newlife.Connect_multiple.service.IModuleService;
import com.newlife.Connect_multiple.service.IProbeModuleService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class ModuleController {

    @Autowired
    private IModuleService moduleService;
    @Autowired
    private IProbeModuleService probeModuleService;

    @GetMapping("/modules")
    public List<ModuleDto> findAllModule(@RequestParam("name")Optional<String> name) {
        List<ModuleDto> listModules = moduleService.findAllModule(name.orElse(""));
        return listModules;
    }

    @GetMapping("/probe/modules")
    public List<ProbeModuleDto> findAllProbeModule(@RequestParam("name") Optional<String> name,
                                                   @RequestParam("status") Optional<String> status,
                                                   @RequestParam("page") Optional<Integer> page,
                                                   @RequestParam("sortBy") Optional<String> sortBy) {
        List<ProbeModuleDto> listProbeModules = probeModuleService.findAllProbeModule(name.orElse(""),
                                                        status.orElse(""),
                                                        page.orElse(0),
                                                        sortBy.orElse("id_probe_module"));
        return listProbeModules;
    }

    @PostMapping("/probeModule")
    public ResponseEntity<?> runModule(@RequestParam("idProbeModule") Optional<Integer> idProbeModule) {
        Object jsonObject = probeModuleService.runModule(idProbeModule.orElse(0));
        return ResponseEntity.ok(jsonObject);
    }
}
