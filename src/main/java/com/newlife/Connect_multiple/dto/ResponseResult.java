package com.newlife.Connect_multiple.dto;

import lombok.Data;

@Data
public class ResponseResult {
    private String result;

    public ResponseResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
