package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.dto.InfoDatabase;
import com.newlife.Connect_multiple.entity.DatabaseServerMysql;
import com.newlife.Connect_multiple.repository.DatabaseServerRepository;
import com.newlife.Connect_multiple.service.IManagementDatabase;
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
public class ManagementDatabase implements IManagementDatabase {

    @Autowired
    private DatabaseServerRepository databaseServerRepository;

    @Override
    public List<InfoDatabase> getAllDatabase(String ipServer) {
        List<DatabaseServerMysql> listServer = new ArrayList<>();
        List<InfoDatabase> listDatabase = new ArrayList<>();
        // TH lay ra toan bo database cua toan bo cac server trong csdl
        if(ipServer == null) {
            listServer = databaseServerRepository.findAllDatabaseServerByKey("");
        }
        else {
            DatabaseServerMysql databaseServerMysql = databaseServerRepository.findByIpServer(ipServer).orElse(null);
            if(databaseServerMysql == null) {
                return null; // TH khong ton tai server co dia chi ip can tim
            }
            listServer.add(databaseServerMysql);
        }

        for(DatabaseServerMysql server : listServer) {
            String url = "jdbc:clickhouse://" + server.getIpServer() + ":" + server.getPortNumber();
            String username = server.getDbAccount();
            String pass = CreateTokenUtil.deCodePass(server.getDbPass());
            Connection connection = null;
            Statement statement = null;
            ResultSet resultSet = null;
            try {
                connection = DriverManager.getConnection(url, username, pass);
                statement = connection.createStatement();
                String query = "SELECT database, " +
                                "formatReadableSize(sum(data_compressed_bytes)) AS total_volume, " +
                                "formatReadableSize(sum(data_uncompressed_bytes)) AS used_volume " +
                                "FROM system.parts " +
                                "GROUP BY database";
                resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    String databaseName = resultSet.getString("database");
                    Long totalSize = resultSet.getLong("total_size");
                    InfoDatabase infoDatabase = new InfoDatabase();

                    infoDatabase.setIp(server.getIpServer());
                    infoDatabase.setType("Clickhouse SSD");
                    infoDatabase.setVolume_total(totalSize);
                }
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
        }
        return null;
    }

}
