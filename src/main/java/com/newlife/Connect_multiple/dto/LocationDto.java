package com.newlife.Connect_multiple.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
@Data
@Getter
@Setter
public class LocationDto {

    private Integer id;
    private String name;
    private String code;
    private List<AreaDto> listArea;

}
