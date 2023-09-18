package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.converter.LocationConverter;
import com.newlife.Connect_multiple.dto.LocationDto;
import com.newlife.Connect_multiple.entity.LocationEntity;
import com.newlife.Connect_multiple.repository.LocationRepository;
import com.newlife.Connect_multiple.service.ILocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService implements ILocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Override
    public List<LocationDto> findAll() {
        List<LocationDto> listLocation = findAllLocation();
        return listLocation;
    }

    private List<LocationDto> findAllLocation() {
        List<LocationEntity> locationEntities = locationRepository.findAll();
        List<LocationDto> listLocationDto = new ArrayList<>();
        for(LocationEntity entity : locationEntities) {
            listLocationDto.add(LocationConverter.toDto(entity));
        }
        return listLocationDto;
    }
}
