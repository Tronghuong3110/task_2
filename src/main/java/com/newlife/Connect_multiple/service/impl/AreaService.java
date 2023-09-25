package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.entity.AreaEntity;
import com.newlife.Connect_multiple.service.IAreaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AreaService implements IAreaService {
    @Override
    public List<AreaEntity> findAllByLocationId(Integer locationId) {
        return null;
    }
}
