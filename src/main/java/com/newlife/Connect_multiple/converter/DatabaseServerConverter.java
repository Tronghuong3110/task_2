package com.newlife.Connect_multiple.converter;

import com.newlife.Connect_multiple.dto.DatabaseServerDto;
import com.newlife.Connect_multiple.entity.DatabaseServerMysql;
import com.newlife.Connect_multiple.util.CreateTokenUtil;

public class DatabaseServerConverter {

    public static DatabaseServerMysql toEntity(DatabaseServerMysql databaseServerMysql, DatabaseServerDto databaseServerDto) {
        try {
            if(databaseServerDto.getIpServer() != null) {
                databaseServerMysql.setIpServer(databaseServerDto.getIpServer());
            }
            if(databaseServerDto.getServerName() != null) {
                databaseServerMysql.setServerName(databaseServerDto.getServerName());
            }
            if(databaseServerDto.getType() != null) {
                databaseServerMysql.setType(databaseServerDto.getType());
            }
            if(databaseServerDto.getDescription() != null) {
                databaseServerMysql.setDescription(databaseServerDto.getDescription());
            }
            if(databaseServerDto.getDbAccount() != null) {
                databaseServerMysql.setDbAccount(databaseServerDto.getDbAccount());
            }
            if(databaseServerDto.getDbPass() != null) {
                databaseServerMysql.setDbPass(CreateTokenUtil.enCodePass(databaseServerDto.getDbPass()));
            }
            if(databaseServerDto.getSshAccount() != null) {
                databaseServerMysql.setSshAccount(databaseServerDto.getSshAccount());
            }
            if(databaseServerDto.getSshPass() != null) {
                databaseServerMysql.setSshPass(CreateTokenUtil.enCodePass(databaseServerDto.getSshPass()));
            }
            if(databaseServerDto.getNasId() != null) {
                databaseServerMysql.setNasId(databaseServerDto.getNasId());
            }
            if(databaseServerDto.getNasName() != null) {
                databaseServerMysql.setNasName(databaseServerDto.getNasName());
            }
            if(databaseServerDto.getPortNumber() != null) {
                databaseServerMysql.setPortNumber(databaseServerDto.getPortNumber());
            }
            if(databaseServerDto.getPassSudo() != null) {
                databaseServerMysql.setPassSudo(CreateTokenUtil.enCodePass(databaseServerDto.getPassSudo()));
            }
            if(databaseServerDto.getSshPort() != null) {
                databaseServerMysql.setSshPort(databaseServerMysql.getSshPort());
            }
            return databaseServerMysql;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
