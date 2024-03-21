package com.newlife.Connect_multiple.service;

import com.newlife.Connect_multiple.dto.DatabaseServerDto;
import org.json.simple.JSONObject;

import java.util.List;

public interface IDatabaseService {

    List<DatabaseServerDto> findAllDatabaseServer(String key);
    DatabaseServerDto findOne(Integer id);
    JSONObject deleteDatabaseServer(Integer id);
    JSONObject saveDatabaseServer(DatabaseServerDto databaseServer);
    JSONObject update(DatabaseServerDto databaseServerDto);
    JSONObject testConnectDatabase(DatabaseServerDto databaseServerDto);
    JSONObject testSSh(DatabaseServerDto databaseServerDto);
}
