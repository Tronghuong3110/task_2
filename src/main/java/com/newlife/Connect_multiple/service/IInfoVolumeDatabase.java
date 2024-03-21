package com.newlife.Connect_multiple.service;

import com.newlife.Connect_multiple.entity.mysql.InfoVolumeDatabaseEntity;

import java.util.List;

public interface IInfoVolumeDatabase {

    List<InfoVolumeDatabaseEntity> findAll(String databaseName, String ipDb, String type);
}
