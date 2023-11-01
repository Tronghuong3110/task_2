package com.newlife.Connect_multiple.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class FileUploadResponse {
    private String fileName;
    private String downloadUri;
    private long size;
}
