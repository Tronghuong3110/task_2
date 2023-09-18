package com.newlife.Connect_multiple.service;

import com.newlife.Connect_multiple.entity.AreaEntity;
import java.util.*;

public interface IAreaService {
    List<AreaEntity> findAllByLocationId(Integer locationId);
}
