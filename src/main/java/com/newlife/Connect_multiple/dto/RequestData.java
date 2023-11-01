package com.newlife.Connect_multiple.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
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
