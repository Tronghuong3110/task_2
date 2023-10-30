package com.newlife.Connect_multiple.service;

import com.newlife.Connect_multiple.dto.ModuleDto;
import org.json.simple.JSONObject;

import java.util.*;

public interface IModuleService {

    List<ModuleDto> findAllModule(String name);
    ModuleDto findOneModule(Integer id);
    JSONObject deleteModule(Integer id);
    JSONObject saveModule(ModuleDto moduleDto);
    JSONObject updateModule(ModuleDto moduleDto);
}
