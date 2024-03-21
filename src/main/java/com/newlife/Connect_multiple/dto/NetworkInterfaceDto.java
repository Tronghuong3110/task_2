package com.newlife.Connect_multiple.dto;

import lombok.Data;

import javax.persistence.Column;

@Data
public class NetworkInterfaceDto {
    private Integer id;
    private String interfaceName;
    private Integer monitor;
    private Integer idProbe;
    private Integer status;
    private String description;
}
