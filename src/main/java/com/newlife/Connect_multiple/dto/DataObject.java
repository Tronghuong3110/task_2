package com.newlife.Connect_multiple.dto;

import lombok.Data;

import java.util.List;

@Data
public class DataObject {
    private List<Rules> rules;
    private String username;
}
