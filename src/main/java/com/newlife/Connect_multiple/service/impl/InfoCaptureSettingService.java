package com.newlife.Connect_multiple.service.impl;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.newlife.Connect_multiple.dto.*;
import com.newlife.Connect_multiple.entity.DatabaseServerMysql;
import com.newlife.Connect_multiple.entity.NasEntity;
import com.newlife.Connect_multiple.entity.mongodb.DatabaseOfServer;
import com.newlife.Connect_multiple.entity.mysql.InfoDatabaseBackUp;
import com.newlife.Connect_multiple.repository.DatabaseServerRepository;
import com.newlife.Connect_multiple.repository.InfoDatabaseBackupRepository;
import com.newlife.Connect_multiple.repository.NasRepository;
import com.newlife.Connect_multiple.service.IInfoCaptureSettingService;
import com.newlife.Connect_multiple.util.CreateTokenUtil;
import org.json.simple.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class InfoCaptureSettingService implements IInfoCaptureSettingService {

    @Autowired
    private DatabaseServerRepository databaseServerRepository;
    @Autowired
    private NasRepository nasRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private InfoDatabaseBackupRepository infoDatabaseBackupRepository;

    @Override
    public List<InfoCaptureSetting> findAll(String probeName, String province, Boolean[] monitorStatus, String backupStatus) {
        // chú ý: có nên thêm lấy ra của 1 server không????
        // lấy ra toàn bộ thông tin trong db mongo(1 danh sách) ==> với mỗi item trong danh sách đại diện cho 1 database 1 probe
        // mỗi probe này sẽ có thông tin db_connection.database_name map với 1 hàng trong bảng info_capture_setting
        // trong mỗi server clickhouse (lấy ra bằng cách connect tới server clickhouse sử dụng thông tin từ entity databaseServer
        try {
            System.out.println("Probe Name " + probeName);
//            System.out.println("Disabled " + monitorStatus);
            Pattern pattern = Pattern.compile(probeName , Pattern.CASE_INSENSITIVE);
            Query query = new Query();
            query.addCriteria(Criteria.where("columns.name").regex(pattern));
            query.addCriteria(Criteria.where("disabled").in(monitorStatus));

            List<DatabaseOfServer> probes = mongoTemplate.find(query, DatabaseOfServer.class);
            List<DatabaseServerMysql> listServer = databaseServerRepository.findAllDatabaseServerByKey("");
            List<InfoCaptureSetting> listResponse = new ArrayList<>();
            System.out.println("Size " + probes.size());

            for(DatabaseServerMysql server : listServer) {
                for(DatabaseOfServer probe : probes) {
                    InfoCaptureSetting infoCaptureSetting = getInfoCaptureSetting(province, backupStatus, probe.getDb_connection().getDatabase_name(), server);
                    if(infoCaptureSetting == null) {
                        continue;
                    }
                    System.out.println("Monitor " + probe.getDisabled());
                    infoCaptureSetting.setProbeName(probe.getColumns().getName());
                    infoCaptureSetting.setDbName(probe.getDb_connection().getDatabase_name());
                    infoCaptureSetting.setIpDbRunning(probe.getDb_connection().getServer());
                    // disabled == true ==> no monitor
                    infoCaptureSetting.setStatusMonitor(probe.getDisabled().equals(true) ? "No Monitor" : "Monitor");
                    infoCaptureSetting.setStartTime(probe.getColumns().getStart_monitor());
                    infoCaptureSetting.setStopTime(probe.getColumns().getStop_monitor());
                    infoCaptureSetting.setIdServer(server.getId());
                    infoCaptureSetting.setNasId(server.getNasId());
                    listResponse.add(infoCaptureSetting);
                }
            }
            return listResponse;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public JSONObject backUpDatabase(Integer idServer, String databaseName, Integer id_info_capture_setting) {
        JSONObject response = new JSONObject();
        DatabaseServerMysql server = databaseServerRepository.findById(idServer).orElse(null);
        // trường hợp không tồn tại server chứa database cần backup
        if(server == null) {
            response.put("code", "0");
            response.put("message", "Can not found database server");
            return response;
        }

        // bắt đầu quá trình ssh và thực hiện backup
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(server.getSshAccount(), server.getIpServer(), server.getSshPort()); // port mặc định là port 22
            session.setPassword(CreateTokenUtil.deCodePass(server.getSshPass()));
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(); // connect ssh server

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            String sudoCommand = "echo " + CreateTokenUtil.deCodePass(server.getPassSudo()) + " | sudo -S ";
            String command = sudoCommand + "service clickhouse-server start; ";
            command += sudoCommand + "rm -r /var/lib/clickhouse/backup/*; ";
            command += sudoCommand + " rm -r /var/lib/clickhouse/shadow/*; ";

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime startBackUp = LocalDateTime.now();
            String startBackUpStr = formatter.format(startBackUp).replaceAll("[/:\\s+]", "_");
            System.out.println("Time backup " + startBackUpStr);
            String nameDatabaseNas = databaseName+"_"+startBackUpStr;

            updateBackupStatus("Processing " + formatter.format(LocalDateTime.now()), server, id_info_capture_setting, null, null);

            command += sudoCommand + "clickhouse-backup create_remote -t " + databaseName + ".* " + nameDatabaseNas;
            channel.setCommand(command);
            channel.setInputStream(null);
            BufferedReader input = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();

            String line;
            String message = "";
            while ((line = input.readLine()) != null) {
                if(line.contains(" done ")) {
                    message += line + "\\n";
                }
                else if(line.contains(" error ")) {
                    System.out.println("Line " + line);
                    response.put("code", 0);
                    message += line + "\\n";
                    LocalDateTime localDateTime = LocalDateTime.now();
                    updateBackupStatus(line.substring(line
                            .indexOf("error")).replaceAll("can't", "can not") + formatter.format(localDateTime), server, id_info_capture_setting, null, null);
                }
            }
            channel.disconnect();
            session.disconnect();
            // trường hợp backup thành công ==> code = 1
            if(!response.containsKey("code")) { // ==> update lại trường backup_status = finished + thời gian
                response.put("code", 1);
                InfoDatabaseBackUp infoDatabaseBackUp = new InfoDatabaseBackUp();
                infoDatabaseBackUp.setDatabaseName(nameDatabaseNas);
                infoDatabaseBackUp.setTimeBackup(formatter.format(startBackUp));
                try {
//                    System.out.println("Back up!!");
                    infoDatabaseBackUp = infoDatabaseBackupRepository.save(infoDatabaseBackUp);
                    LocalDateTime localDateTime = LocalDateTime.now();
                    updateBackupStatus("Finished " + formatter.format(localDateTime), server, id_info_capture_setting, null, null);
                    response.put("id_info_database_backup", infoDatabaseBackUp.getId());
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Save info database backup error!!!!");
                }
            }
            response.put("message", message);
//            return response;
        } catch (Exception e) {
            response.put("code", 0);
            response.put("message", "Backup error");
            updateBackupStatus("Error " + e.getMessage(), server, id_info_capture_setting, null, null);
            System.out.println("Đã xảy ra lỗi: " + e.getMessage());
        }
        return response;
    }

    @Override
    public void restoreDatabase(Integer idServer, Integer idInfoDatabase) {
        JSONObject response = new JSONObject();
        DatabaseServerMysql server = databaseServerRepository.findById(idServer).orElse(null);
        InfoDatabaseBackUp infoDatabaseBackUp = infoDatabaseBackupRepository.findById(idInfoDatabase).orElse(null);
        // trường hợp không tồn tại server chứa database cần backup
        if(server == null) {
            response.put("code", "0");
            response.put("message", "Can not found database server");
            return;
        }
        if(infoDatabaseBackUp == null) {
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(server.getSshAccount(), server.getIpServer(), server.getSshPort()); // port mặc định là port 22
            session.setPassword(CreateTokenUtil.deCodePass(server.getSshPass()));
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(); // connect ssh server

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            String sudoCommand = "echo " + CreateTokenUtil.deCodePass(server.getPassSudo()) + " | sudo -S ";
            String command = sudoCommand + "service clickhouse-server start; ";
            // update time start restore
            infoDatabaseBackUp.setTimeStartRestore(formatter.format(LocalDateTime.now()));
            infoDatabaseBackUp.setRestoreStatus("Processing");
            infoDatabaseBackupRepository.save(infoDatabaseBackUp);
            command += sudoCommand + "clickhouse-backup download " + infoDatabaseBackUp.getDatabaseName();
            command += sudoCommand + "clickhouse-backup restore_remote " + infoDatabaseBackUp.getDatabaseName();
            channel.setCommand(command);
            channel.setInputStream(null);
            BufferedReader input = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();

            String line;
            String message = "";
            while ((line = input.readLine()) != null) {
                if(line.contains(" done ")) {
                    message += line + "\\n";
                }
                else if(line.contains(" error ")) {
                    response.put("code", 0);
                    message += line + "\\n";
                    LocalDateTime localDateTime = LocalDateTime.now();
                    infoDatabaseBackUp.setTimeEndRestore(formatter.format(localDateTime));
                    infoDatabaseBackUp.setRestoreStatus("Error");
                    infoDatabaseBackupRepository.save(infoDatabaseBackUp);
                }
            }
            channel.disconnect();
            session.disconnect();
            // trường hợp backup thành công ==> code = 1
            if(!response.containsKey("code")) { // ==> update lại trường backup_status = finished + thời gian
                response.put("code", 1);
                infoDatabaseBackUp.setTimeEndRestore(formatter.format(LocalDateTime.now()));
                infoDatabaseBackUp.setRestoreStatus("Finished");
                infoDatabaseBackupRepository.save(infoDatabaseBackUp);
            }
            response.put("message", message);
//            return response;
        } catch (Exception e) {
            response.put("code", 0);
            response.put("message", "Backup error");
            System.out.println("Đã xảy ra lỗi: " + e.getMessage());
            infoDatabaseBackUp.setTimeEndRestore(formatter.format(LocalDateTime.now()));
            infoDatabaseBackUp.setRestoreStatus("Error");
            try {
                infoDatabaseBackupRepository.save(infoDatabaseBackUp);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
        }
//        return response;
//        return;
    }

    @Override
    public List<InfoDatabaseBackup> findAllInfo(String databaseName, Integer idInfo) {
        try {
            List<InfoDatabaseBackUp> listInfo = infoDatabaseBackupRepository.findAllGroupById(databaseName, idInfo);
            List<InfoDatabaseBackup> listResponses = new ArrayList<>();
            for(InfoDatabaseBackUp info : listInfo) {
                InfoDatabaseBackup infoDatabaseBackup = new InfoDatabaseBackup();
                BeanUtils.copyProperties(info, infoDatabaseBackup);
                listResponses.add(infoDatabaseBackup);
            }
            return listResponses;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public InfoCaptureBackup findOneInfo(Integer idServer, Integer idNas) {
        try {
            InfoCaptureBackup infoCaptureBackup = new InfoCaptureBackup();
            DatabaseServerMysql databaseServer = databaseServerRepository.findById(idServer).orElse(null);
            NasEntity nas = nasRepository.findById(idNas).orElse(null);
            NasDto nasDto = new NasDto();
            BeanUtils.copyProperties(nas, nasDto);
            DatabaseServerDto databaseServerDto = new DatabaseServerDto();
            BeanUtils.copyProperties(databaseServer, databaseServerDto);
            infoCaptureBackup.setDatabaseServer(databaseServerDto);
            infoCaptureBackup.setNasDto(nasDto);
            return infoCaptureBackup;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject deleteDatabase(String ipServer, String databaseName, Integer id_info_capture_setting) {
        DatabaseServerMysql server = databaseServerRepository.findByIpServer(ipServer).orElse(null);
        JSONObject response = new JSONObject();
        if(server == null) {
            response.put("code", 0);
            response.put("message", "Can not found database server");
            return response;
        }
        String url = "jdbc:clickhouse://" + server.getIpServer() + ":" + server.getPortNumber();
        String username = server.getDbAccount();
        String pass = CreateTokenUtil.deCodePass(server.getDbPass());
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(url, username, pass);
            statement = connection.createStatement();
            String sql = "drop database " + databaseName;
            statement.executeQuery(sql);
            response.put("code", 1);
            response.put("message", "Delete database success");
            if(server.getType().equals("SSD")) { // xoa tren server 1
                updateBackupStatus(null, server, id_info_capture_setting, 1, "delete_db_l1");
            }
            else if(server.getType().equals("HDD")) { // xoa tren server 2
                updateBackupStatus(null, server, id_info_capture_setting, 1, "delete_db_l2");
            }
        }
        catch (Exception e) {
            response.put("code", 0);
            response.put("message", "Delete database failed");
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
        return response;
    }

    @Override
    public List<String> findAllDatabaseNameOfServer(Integer idServer) {
        DatabaseServerMysql server = databaseServerRepository.findById(idServer).orElse(null);
        if(server == null) {
            return null;
        }
        String url = "jdbc:clickhouse://" + server.getIpServer() + ":" + server.getPortNumber();
        String username = server.getDbAccount();
        String pass = CreateTokenUtil.deCodePass(server.getDbPass());
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<String> listDatabaseName = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(url, username, pass);
            statement = connection.createStatement();
            String sql = "show databases";
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String databaseName = resultSet.getString("name");
                listDatabaseName.add(databaseName);
            }
            return listDatabaseName;
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

    private InfoCaptureSetting getInfoCaptureSetting(String province, String backupStatus, String dbname, DatabaseServerMysql server) {
        String url = "jdbc:clickhouse://" + server.getIpServer() + ":" + server.getPortNumber();
        String username = server.getDbAccount();
        String pass = CreateTokenUtil.deCodePass(server.getDbPass());
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            // connect to clickhouse server
            connection = DriverManager.getConnection(url, username, pass);
            statement = connection.createStatement();
            String query = "select id, province, backup_status, db_ip, delete_db_l1, db_ip_l2, delete_db_l2 from cem_network_general.info_capture_setting "
                    + "where province like '%" + province + "%' and backup_status like '%" + backupStatus + "%' and db_name = '" + dbname + "' " ;
//            System.out.println("Query " + query);
            resultSet = statement.executeQuery(query);
            InfoCaptureSetting infoCaptureSetting = new InfoCaptureSetting();
            int totalRow = 0;
            while(resultSet.next()) {
                totalRow++;
                infoCaptureSetting.setBackupStatus(resultSet.getString("backup_status"));
                infoCaptureSetting.setProvince(resultSet.getString("province"));
                String checkIp1 = resultSet.getString("delete_db_l1");
                String checkIp2 = resultSet.getString("delete_db_l2");
                String ipDb1 = resultSet.getString("db_ip");
                String ipDb2 = resultSet.getString("db_ip_l2");
                // da xoa thi hien thi them deleted
                infoCaptureSetting.setIpDbLevel1(checkIp1.equals("1") ? ipDb1+" (Deleted)" : ipDb1);
                infoCaptureSetting.setIpDbLevel2(checkIp2.equals("1") ? ipDb2+" (Deleted)" : ipDb2);
                infoCaptureSetting.setId_info_capture_setting(resultSet.getInt("id"));
            }
            if(totalRow < 1) {
                return null;
            }
            return infoCaptureSetting;
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
        return new InfoCaptureSetting();
    }

    private void updateBackupStatus(String status, DatabaseServerMysql server, Integer id_info_capture_setting, Integer delete_db, String delete_ip_db_name) {
        String url = "jdbc:clickhouse://" + server.getIpServer() + ":" + server.getPortNumber();
        String username = server.getDbAccount();
        String pass = CreateTokenUtil.deCodePass(server.getDbPass());
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            // connect to clickhouse server
            connection = DriverManager.getConnection(url, username, pass);
            statement = connection.createStatement();
            String query = "alter table cem_network_general.info_capture_setting update " ;
            if(status != null) {
                query += "backup_status = '" + status + "' ";
            }
            if(delete_ip_db_name != null) {
                if(status != null) {
                    query += " and ";
                }
                query += delete_ip_db_name + " = '" + delete_db + "' ";
            }
            query += " where id = " + id_info_capture_setting ;
            statement.execute(query);
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
}
