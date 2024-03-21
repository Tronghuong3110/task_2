package com.newlife.Connect_multiple.service;

import com.mongodb.util.JSON;
import com.newlife.Connect_multiple.dto.DatabaseServerDto;
import com.newlife.Connect_multiple.dto.InfoCaptureBackup;
import com.newlife.Connect_multiple.dto.InfoCaptureSetting;
import com.newlife.Connect_multiple.dto.InfoDatabaseBackup;
import com.newlife.Connect_multiple.repository.DatabaseServerRepository;
import org.json.simple.JSONObject;

import java.util.List;

public interface IInfoCaptureSettingService {

    List<InfoCaptureSetting> findAll();
    JSONObject backUpDatabase(Integer idServer, String databaseName, Integer id_info_capture_setting, String restoreNow);
    void restoreDatabase(Integer idServer, Integer idInfoDatabase);
    List<InfoDatabaseBackup> findAllInfo(String databaseName, Integer idInfo);
    InfoCaptureBackup findOneInfo(Integer idServer, Integer idNas);
    JSONObject deleteDatabase(String ipServer, String databaseName, Integer id_info_capture_setting);
    List<String> findAllDatabaseNameOfServer(Integer idServer);
    List<InfoCaptureSetting> findAllInfoCaptureSetting();
    List<InfoDatabaseBackup> findAllByDatabaseNameAndTimeBackup(String databaseName);
}
