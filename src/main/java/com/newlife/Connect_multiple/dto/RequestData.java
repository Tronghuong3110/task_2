package com.newlife.Connect_multiple.dto;

import lombok.Data;

@Data
public class RequestData {
    private ProbeDto probeDto;
    private ProbeOptionDto probeOptionDto;

    public ProbeDto getProbeDto() {
        return probeDto;
    }

    public void setProbeDto(ProbeDto probeDto) {
        this.probeDto = probeDto;
    }

    public ProbeOptionDto getProbeOptionDto() {
        return probeOptionDto;
    }

    public void setProbeOptionDto(ProbeOptionDto probeOptionDto) {
        this.probeOptionDto = probeOptionDto;
    }
}
