package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.entity.DatabaseServerMysql;
import com.newlife.Connect_multiple.entity.mysql.InfoVolumeDatabaseEntity;
import com.newlife.Connect_multiple.repository.DatabaseServerRepository;
import com.newlife.Connect_multiple.service.IInfoVolumeDatabase;
import com.newlife.Connect_multiple.util.CreateTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Service
public class InfoVolumeDatabase implements IInfoVolumeDatabase {

    @Autowired
    private DatabaseServerRepository databaseServerRepository;

    @Override
    public List<InfoVolumeDatabaseEntity> findAll(String databaseName, String ipDb, String type) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            List<DatabaseServerMysql> listDatabaseServer = databaseServerRepository.findByIpOrType(ipDb, type);
            List<InfoVolumeDatabaseEntity> listResponse = new ArrayList<>();
            for(DatabaseServerMysql server : listDatabaseServer) {
                String url = "jdbc:clickhouse://" + server.getIpServer() + ":" + server.getPortNumber();
                String username = server.getDbAccount();
                String pass = CreateTokenUtil.deCodePass(server.getDbPass());
                connection = DriverManager.getConnection(url, username, pass);
                statement = connection.createStatement();
                String sql = "SELECT database, " +
                        "formatReadableSize(sum(bytes) + sum(marks_size)) AS total_volume, " +
                        "formatReadableSize(sum(data_uncompressed_bytes)) AS used_volume, " +
                        "formatReadableSize(sum(bytes) + sum(marks_size) - sum(data_uncompressed_bytes)) AS free_volume " +
                        "FROM system.parts where database like '%" + databaseName + "%' " +
                        "GROUP BY database";
//                System.out.println("Query " + sql);
                resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    InfoVolumeDatabaseEntity infoVolumeDatabase = new InfoVolumeDatabaseEntity();
                    infoVolumeDatabase.setIpDb(server.getIpServer());
                    infoVolumeDatabase.setType(server.getType());
                    infoVolumeDatabase.setVolumeUsed(resultSet.getString("used_volume"));
                    infoVolumeDatabase.setVolumeTotal(resultSet.getString("total_volume"));
                    infoVolumeDatabase.setVolumeFree(resultSet.getString("free_volume"));
                    infoVolumeDatabase.setDatabaseName(resultSet.getString("database"));
                    listResponse.add(infoVolumeDatabase);
                }
            }
            return listResponse;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(connection != null) {
                    connection.close();
                }
                if(statement != null) {
                    statement.close();
                }
                if(resultSet != null) {
                    resultSet.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
