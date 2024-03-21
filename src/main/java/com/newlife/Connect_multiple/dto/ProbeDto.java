package com.newlife.Connect_multiple.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@Getter
@Setter
public class ProbeDto {
    private Integer id;
    private String location;
    private String area; //
    private String name;
    private String status; // đại diện cho probe còn kết nối tới broker không
    private String ipAddress;
    private Timestamp connectAt;
    private String numberStopedModule;
    private String numberRunningModule;
    private String numberPendingModule;
    private String numberFailedModule;
    private String olt;
    private Integer vlan;
    private String description;
    private Integer totalModule;
    private String message;
    private Long totalPage;
    private Boolean pending;
    private BigInteger numberInterfaceUp;
    private BigInteger numberInterfaceDown;
    private String sshAccount;
    private String sshPass;
    private Integer sshPort;
    private String sudoPass;
}
