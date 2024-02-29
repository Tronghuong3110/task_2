package com.newlife.Connect_multiple.dto;

import lombok.Data;

@Data
public class InfoDatabase {
    private String name;
    private String ip;
    private String type;
    private Long volume_total;
    private Long volume_used;
    private Long volume_free;
}
