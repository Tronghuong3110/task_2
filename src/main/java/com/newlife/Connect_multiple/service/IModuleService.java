package com.newlife.Connect_multiple.service;

import com.newlife.Connect_multiple.dto.ModuleDto;
import java.util.*;

public interface IModuleService {

    List<ModuleDto> findAllModule(String name);
    ModuleDto findOneModule(Integer id);
    String deleteModule(Integer id);
    String saveModule(ModuleDto moduleDto);

    String updateModule(ModuleDto moduleDto);
}
