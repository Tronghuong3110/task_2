package com.newlife.Connect_multiple.service;

import com.newlife.Connect_multiple.dto.InfoDatabase;

import java.util.List;

public interface IManagementDatabase {
    List<InfoDatabase> getAllDatabase(String ipServer);
}
