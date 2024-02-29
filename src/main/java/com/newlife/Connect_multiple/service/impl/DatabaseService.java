package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.converter.DatabaseServerConverter;
import com.newlife.Connect_multiple.dto.DatabaseServerDto;
import com.newlife.Connect_multiple.entity.DatabaseServerMysql;
import com.newlife.Connect_multiple.repository.DatabaseServerRepository;
import com.newlife.Connect_multiple.service.IDatabaseService;
import com.newlife.Connect_multiple.util.CreateTokenUtil;
import org.json.simple.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseService implements IDatabaseService {

    @Autowired
    private DatabaseServerRepository databaseServerRepository;

    @Override
    public List<DatabaseServerDto> findAllDatabaseServer(String key) {
        try {
            List<DatabaseServerMysql> listDatabaseServerMysql = databaseServerRepository.findAllDatabaseServerByKey(key);
            List<DatabaseServerDto> listResponse = new ArrayList<>();
            for(DatabaseServerMysql server : listDatabaseServerMysql) {
                DatabaseServerDto tmp = new DatabaseServerDto();
                BeanUtils.copyProperties(server, tmp);
                tmp.setDbPass(CreateTokenUtil.deCodePass(server.getDbPass()));
                listResponse.add(tmp);
            }
            return listResponse;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public DatabaseServerDto findOne(Integer id) {
        try {
            DatabaseServerMysql databaseServerMysql = databaseServerRepository.findById(id)
                    .orElse(null);
            DatabaseServerDto response = new DatabaseServerDto();
            BeanUtils.copyProperties(databaseServerMysql, response);
            System.out.println("Pass " + databaseServerMysql.getDbPass());
            System.out.println("Pass " + databaseServerMysql.getSshPass());
            String passDBDecode = CreateTokenUtil.deCodePass(databaseServerMysql.getDbPass());
            String passSSHDecode = CreateTokenUtil.deCodePass(databaseServerMysql.getSshPass());
            response.setDbPass(passDBDecode);
            response.setSshPass(passSSHDecode);
            return response;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public JSONObject deleteDatabaseServer(Integer id) {
        JSONObject jsonObject = new JSONObject();
        try {
            databaseServerRepository.deleteById(id);
            jsonObject.put("code", 1);
            jsonObject.put("message", "Delete database sever with id = " + id + " success");
            return jsonObject;
        }
        catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("code", 0);
            jsonObject.put("message", "Delete database sever with id = " + id + " fail");
            return jsonObject;
        }
    }
    @Override
    public JSONObject saveDatabaseServer(DatabaseServerDto databaseServer) {
        JSONObject jsonObject = new JSONObject();
        Boolean checkExist = databaseServerRepository.existsByIpServer(databaseServer.getIpServer());
        try {
            // ip da ton tai trong csdl
            if(checkExist) {
                jsonObject.put("code", 0);
                jsonObject.put("message", "Ip server have been duplicated");
                return jsonObject;
            }
            DatabaseServerMysql dbServer = new DatabaseServerMysql();
            BeanUtils.copyProperties(databaseServer, dbServer);
            String passDBEncode = CreateTokenUtil.enCodePass(databaseServer.getDbPass());
            String passSSHEncode = CreateTokenUtil.enCodePass(databaseServer.getSshPass());
            String passSudo = CreateTokenUtil.enCodePass(databaseServer.getPassSudo());
            dbServer.setDbPass(passDBEncode);
            dbServer.setSshPass(passSSHEncode);
            dbServer.setPassSudo(passSudo);
            databaseServerRepository.save(dbServer);
            jsonObject.put("code", 1);
            jsonObject.put("message", "Add database server success");
            return jsonObject;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        jsonObject.put("code", 0);
        jsonObject.put("message", "Add database server fail");
        return jsonObject;
    }
    @Override
    public JSONObject update(DatabaseServerDto databaseServerDto) {
        JSONObject jsonObject = new JSONObject();
        DatabaseServerMysql databaseServerMysql = databaseServerRepository.findById(databaseServerDto.getId()).orElse(null);
        if(databaseServerMysql == null) {
            jsonObject.put("code", 0);
            jsonObject.put("message", "Can not found database server with id = " + databaseServerDto.getId());
            return jsonObject;
        }
        databaseServerMysql = DatabaseServerConverter.toEntity(databaseServerMysql, databaseServerDto);
        if(databaseServerMysql == null) {
            jsonObject.put("code", 0);
            jsonObject.put("message", "Can not convert database server");
            return jsonObject;
        }
        databaseServerRepository.save(databaseServerMysql);
        jsonObject.put("code", 1);
        jsonObject.put("message", "Update database server success");
        return jsonObject;
    }

}
