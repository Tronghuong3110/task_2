package com.newlife.Connect_multiple.service;

import com.mongodb.util.JSON;
import com.newlife.Connect_multiple.dto.InfoCaptureBackup;
import com.newlife.Connect_multiple.dto.InfoCaptureSetting;
import com.newlife.Connect_multiple.dto.InfoDatabaseBackup;
import org.json.simple.JSONObject;

import java.util.List;

public interface IInfoCaptureSettingService {

    List<InfoCaptureSetting> findAll(String probeName, String province, Boolean[] monitorStatus, String backupStatus);
    JSONObject backUpDatabase(Integer idServer, String databaseName, Integer id_info_capture_setting);
    void restoreDatabase(Integer idServer, Integer idInfoDatabase);
    List<InfoDatabaseBackup> findAllInfo(String databaseName, Integer idInfo);
    InfoCaptureBackup findOneInfo(Integer idServer, Integer idNas);
    JSONObject deleteDatabase(String ipServer, String databaseName, Integer id_info_capture_setting);
    List<String> findAllDatabaseNameOfServer(Integer idServer);
}
