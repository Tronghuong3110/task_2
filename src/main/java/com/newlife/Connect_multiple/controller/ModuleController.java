package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.dto.ModuleDto;
import com.newlife.Connect_multiple.service.IModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class ModuleController {

    @Autowired
    private IModuleService moduleService;

    @GetMapping("/modules")
    public List<ModuleDto> findAllModule(@RequestParam("name")Optional<String> name) {
        return null;
    }
}
