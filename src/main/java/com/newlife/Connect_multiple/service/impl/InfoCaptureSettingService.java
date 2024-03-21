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
import com.newlife.Connect_multiple.repository.IInfocaptureRepository;
import com.newlife.Connect_multiple.repository.InfoDatabaseBackupRepository;
import com.newlife.Connect_multiple.repository.NasRepository;
import com.newlife.Connect_multiple.service.IInfoCaptureSettingService;
import com.newlife.Connect_multiple.util.CreateTokenUtil;
import org.json.simple.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import java.util.concurrent.*;

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
    @Autowired
    private IInfocaptureRepository infocaptureRepository;

    @Override
    @Transactional
    public List<InfoCaptureSetting> findAll() {
        // chú ý: có nên thêm lấy ra của 1 server không????
        // lấy ra toàn bộ thông tin trong db mongo(1 danh sách) ==> với mỗi item trong danh sách đại diện cho 1 database 1 probe
        // mỗi probe này sẽ có thông tin db_connection.database_name map với 1 hàng trong bảng info_capture_setting
        // trong mỗi server clickhouse (lấy ra bằng cách connect tới server clickhouse sử dụng thông tin từ entity databaseServer
        try {
//            Pattern pattern = Pattern.compile(probeName , Pattern.CASE_INSENSITIVE);
            Query query = new Query();
//            query.addCriteria(Criteria.where("columns.name").regex(pattern));
//            query.addCriteria(Criteria.where("disabled").in(monitorStatus));

            List<DatabaseOfServer> probes = mongoTemplate.find(query, DatabaseOfServer.class);
            List<DatabaseServerMysql> listServer = databaseServerRepository.findAllDatabaseServerByKey("");
            infocaptureRepository.deleteAll();
            for(DatabaseServerMysql server : listServer) {
                List<InfoCaptureSetting> listResponse = new ArrayList<>();
                Boolean check = false;
                for(DatabaseOfServer probe : probes) {
                    InfoCaptureSetting infoCaptureSetting = getInfoCaptureSetting(probe.getDb_connection().getDatabase_name(), server);
                    if(infoCaptureSetting == null) {
                        continue;
                    }
                    // TH server mất kết nối đến clickhouse server
                    if(infoCaptureSetting.getId_info_capture_setting() == null && infoCaptureSetting.getStatus_connect() != null && infoCaptureSetting.getStatus_connect().equals(0)) {
//                        System.out.println("Connect time out!!!");
//                        listResponse.add(infoCaptureSetting);
//                        System.out.println("Id Server(LINE: 78) " + server.getId());
                        infocaptureRepository.deleteAllByIdServer(server.getId());
                        infocaptureRepository.save(infoCaptureSetting);
                        check = true;
                        break;
                    }
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
                if(!check) {
//                    System.out.println("Id Server(LINE: 94) " + server.getId());
                    infocaptureRepository.deleteAllByIdServer(server.getId());
                    infocaptureRepository.saveAll(listResponse);
                }
            }
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public JSONObject backUpDatabase(Integer idServer, String databaseName, Integer id_info_capture_setting, String restoreNow) {
        JSONObject response = new JSONObject();
        InfoDatabaseBackUp infoDatabaseBackUp = new InfoDatabaseBackUp();
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

            // đếm số bangr của database đang backup
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            String commandReadTotalTable = "clickhouse-client --password='" + CreateTokenUtil.deCodePass(server.getDbPass()) + "' --query=\"SELECT count() FROM system.tables WHERE database = '" + databaseName + "' \"";
            channel.setCommand(commandReadTotalTable);
            channel.setInputStream(null);
            channel.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            String line = reader.readLine();
            Integer totaltable = 0;
            totaltable = Integer.parseInt(line);
            channel.disconnect();

            // chuẩn bị các thông tin để backup
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime startBackUp = LocalDateTime.now();
            String startBackUpStr = formatter.format(startBackUp).replaceAll("[/:\\s+]", "_");
            System.out.println("Time backup " + startBackUpStr);
            String nameDatabaseNas = databaseName+"_"+startBackUpStr;
            infoDatabaseBackUp.setDatabaseName(nameDatabaseNas);
            infoDatabaseBackUp.setTimeBackup(formatter.format(startBackUp));
            infoDatabaseBackUp.setTotalTable(totaltable);
            updateBackupStatus("Processing ", server, id_info_capture_setting, null, null);
            infoDatabaseBackUp.setBackupStatus("Processing");
            infoDatabaseBackUp.setBackupProcess((double)0);
            infoDatabaseBackUp.setRestoreProcess((double)0);
            if(restoreNow.equals("true")) {
                infoDatabaseBackUp.setRestoreStatus("Waiting for restore");
            }
            infoDatabaseBackUp = infoDatabaseBackupRepository.save(infoDatabaseBackUp);

            // thực hiện backup
            channel = (ChannelExec) session.openChannel("exec");
            String sudoCommand = "echo " + CreateTokenUtil.deCodePass(server.getPassSudo()) + " | sudo -S ";
            String command = sudoCommand + "service clickhouse-server start; ";
            command += sudoCommand + "rm -r /var/lib/clickhouse/backup/*; ";
            command += sudoCommand + " rm -r /var/lib/clickhouse/shadow/*; ";

            command += sudoCommand + "clickhouse-backup create_remote -t " + databaseName + ".* " + nameDatabaseNas;
            channel.setCommand("nohup " + command + " &");
            channel.setInputStream(null);
            channel.connect();

            BufferedReader input = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            Integer tableCurrent = 0;
            Integer dataUpload = 0;
            while ((line = input.readLine()) != null) {
                if(line.contains(" error ")) {
                    System.out.println("Line " + line);
                    response.put("code", 0);
                    LocalDateTime localDateTime = LocalDateTime.now();
                    String status = line.substring(line.indexOf("error")).replaceAll("can't", "can not") + formatter.format(localDateTime);
                    updateBackupStatus(status, server, id_info_capture_setting, null, null);
                    infoDatabaseBackUp.setBackupStatus(status);
                    infoDatabaseBackUp.setRestoreStatus("Backup database error, can not restore database now");
                    infoDatabaseBackupRepository.save(infoDatabaseBackUp);
                    break;
                }
                else if(line.contains(" ALTER TABLE ") && line.contains(" FREEZE WITH NAME ")) {
                    tableCurrent++;
                    infoDatabaseBackUp.setBackupProcess((double)tableCurrent/totaltable * 100);
                    System.out.println("Process backup = " + ((double)tableCurrent/totaltable));
                    infoDatabaseBackUp = infoDatabaseBackupRepository.save(infoDatabaseBackUp);
                }
                else if (line.contains(" done ") && line.contains(" operation=upload ")) {
                    dataUpload++;
                    infoDatabaseBackUp.setBackupProcess((double)dataUpload/totaltable * 100);
                    System.out.println("Process backup(upload data) = " + ((double)dataUpload/totaltable));
                    if((double)dataUpload/totaltable * 100 == 100.0) {
                        response.put("code", 1);
                        infoDatabaseBackUp.setBackupStatus("Finished " + formatter.format(LocalDateTime.now()));
                        updateBackupStatus("Finished " + formatter.format(LocalDateTime.now()), server, id_info_capture_setting, null, null);
                        infoDatabaseBackupRepository.save(infoDatabaseBackUp);
                        break;
                    }
                    infoDatabaseBackUp = infoDatabaseBackupRepository.save(infoDatabaseBackUp);
                }
            }
            channel.disconnect();
            session.disconnect();
            response.put("id_info_database_backup", infoDatabaseBackUp.getId());
            return response;
        } catch (Exception e) {
            response.put("code", 0);
            response.put("message", "Backup error");
            updateBackupStatus("Error " + e.getMessage(), server, id_info_capture_setting, null, null);
            infoDatabaseBackUp.setBackupStatus("Error " + e.getMessage());
            infoDatabaseBackUp.setRestoreStatus("Backup database error, can not restore database now");
            infoDatabaseBackupRepository.save(infoDatabaseBackUp);
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
        System.out.println("Start restore");
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(server.getSshAccount(), server.getIpServer(), server.getSshPort()); // port mặc định là port 22
            session.setPassword(CreateTokenUtil.deCodePass(server.getSshPass()));
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(); // connect ssh server

            String sudoCommand = "echo " + CreateTokenUtil.deCodePass(server.getPassSudo()) + " | sudo -S ";

            // start download database from nas
            infoDatabaseBackUp.setTimeStartRestore(formatter.format(LocalDateTime.now()));
            infoDatabaseBackUp.setRestoreStatus("Processing");
            infoDatabaseBackUp.setRestoreProcess((double)0);
            infoDatabaseBackUp = infoDatabaseBackupRepository.save(infoDatabaseBackUp);
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            String command = sudoCommand + "clickhouse-backup download " + infoDatabaseBackUp.getDatabaseName();
            channel.setCommand(command);
            channel.setInputStream(null);
            channel.connect();
            String line;
            Integer tableCurrent = 0;
            BufferedReader input = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            while ((line = input.readLine()) != null) {
                if(line.contains(" done ") && line.contains(" operation=download_data ")) {
                    tableCurrent++;
                    Double processrestore = (double)tableCurrent/infoDatabaseBackUp.getTotalTable() * 100;
                    infoDatabaseBackUp.setRestoreProcess(processrestore);
                    infoDatabaseBackUp = infoDatabaseBackupRepository.save(infoDatabaseBackUp);
                    System.out.println("Process restore(Download): " + infoDatabaseBackUp.getRestoreProcess());
                }
                else if(line.contains(" error ")) {
                    response.put("code", 0);
                    System.out.println("Error restore: " + line);
                    LocalDateTime localDateTime = LocalDateTime.now();
                    infoDatabaseBackUp.setTimeEndRestore(formatter.format(localDateTime));
                    infoDatabaseBackUp.setRestoreStatus(line);
                    infoDatabaseBackupRepository.save(infoDatabaseBackUp);
                    return;
                }
            }
            channel.disconnect();

            // start restore database to server
            channel = (ChannelExec) session.openChannel("exec");
            command = sudoCommand + "clickhouse-backup restore_remote " + infoDatabaseBackUp.getDatabaseName();
            channel.setCommand(command);
            channel.setInputStream(null);
            channel.connect();
            input = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            infoDatabaseBackUp = infoDatabaseBackupRepository.save(infoDatabaseBackUp);
            String message = "";
            tableCurrent = 0;
            while ((line = input.readLine()) != null) {
                if(line.contains(" done ") && line.contains(" operation=restore ")) {
                    tableCurrent++;
                    Double processrestore = (double)tableCurrent/infoDatabaseBackUp.getTotalTable() * 100;
                    infoDatabaseBackUp.setRestoreProcess(processrestore);
                    infoDatabaseBackUp = infoDatabaseBackupRepository.save(infoDatabaseBackUp);
                    System.out.println("Process restore(Restore): " + infoDatabaseBackUp.getRestoreProcess());
                }
                else if(line.contains(" error ")) {
                    response.put("code", 0);
                    System.out.println("Error restore: " + line);
                    LocalDateTime localDateTime = LocalDateTime.now();
                    infoDatabaseBackUp.setTimeEndRestore(formatter.format(localDateTime));
                    infoDatabaseBackUp.setRestoreStatus(line);
                    infoDatabaseBackUp = infoDatabaseBackupRepository.save(infoDatabaseBackUp);
                }
            }
            channel.disconnect();
            session.disconnect();
            // trường hợp backup thành công ==> code = 1
            if(!response.containsKey("code")) { // ==> update lại trường backup_status = finished + thời gian
                response.put("code", 1);
                infoDatabaseBackUp.setTimeEndRestore(formatter.format(LocalDateTime.now()));
                infoDatabaseBackUp.setRestoreStatus("Finished ");
                infoDatabaseBackUp = infoDatabaseBackupRepository.save(infoDatabaseBackUp);
            }
            response.put("message", message);
//            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 0);
            response.put("message", "Restore error!!");
            System.out.println("Đã xảy ra lỗi: " + e.getMessage());
            infoDatabaseBackUp.setTimeEndRestore(formatter.format(LocalDateTime.now()));
            infoDatabaseBackUp.setRestoreStatus(e.getMessage());
            try {
                infoDatabaseBackupRepository.save(infoDatabaseBackUp);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
        }
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

    @Override
    public List<InfoCaptureSetting> findAllInfoCaptureSetting() {
        try {
            List<InfoCaptureSetting> listInfo = infocaptureRepository.findAll();
            List<InfoCaptureSetting> listResponse = new ArrayList<>();
            for(InfoCaptureSetting info : listInfo) {
                InfoDatabaseBackUp infoDatabase = infoDatabaseBackupRepository.findAllByDatabaseName(info.getDbName());
                if(infoDatabase != null && (infoDatabase.getBackupStatus().contains("Finished ") || infoDatabase.getBackupStatus().contains("error "))) {
                    infoDatabase = infoDatabaseBackupRepository.findByDatabaseNameAndTimeRestore(info.getDbName());
                }
                if(infoDatabase == null || infoDatabase.getRestoreStatus() == null) {
                    info.setStatusRestore("IDLE");
                }
                else {
                    String time = infoDatabase.getTimeEndRestore() == null ? "":infoDatabase.getTimeEndRestore();
                    String statusRestore = infoDatabase.getRestoreStatus() + " " + time;
                    System.out.println("Status: " + statusRestore);
                    info.setStatusRestore(statusRestore);
                    info.setProcessRestore(infoDatabase.getRestoreProcess());
                    info.setProcessBackup(infoDatabase.getBackupProcess());
                    info.setBackupStatus(infoDatabase.getBackupStatus());
                }
                listResponse.add(info);
            }
            return listResponse;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<InfoDatabaseBackup> findAllByDatabaseNameAndTimeBackup(String databaseName) {
//        try {
//            List<InfoDatabaseBackUp> listInfo = infoDatabaseBackupRepository.findAllByDatabaseName(databaseName);
//            List<InfoDatabaseBackup> listResponse = new ArrayList<>();
//            for(InfoDatabaseBackUp info : listInfo) {
//                InfoDatabaseBackup infoDatabaseBackup = new InfoDatabaseBackup();
//                BeanUtils.copyProperties(info, infoDatabaseBackup);
//                listResponse.add(infoDatabaseBackup);
//            }
//            return listResponse;
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
        return null;
    }

    private InfoCaptureSetting getInfoCaptureSetting(String dbname, DatabaseServerMysql server) {
        String url = "jdbc:clickhouse://" + server.getIpServer() + ":" + server.getPortNumber();
        String username = server.getDbAccount();
        String pass = CreateTokenUtil.deCodePass(server.getDbPass());
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        InfoCaptureSetting infoCaptureSetting = new InfoCaptureSetting();
        try {
            connection = DriverManager.getConnection(url, username, pass);
            statement = connection.createStatement();
            String query = "select id, province, backup_status, db_ip, delete_db_l1, db_ip_l2, delete_db_l2 from cem_network_general.info_capture_setting "
                    + "where db_name = '" + dbname + "' limit 1" ;
//            System.out.println("Query " + query);
            resultSet = statement.executeQuery(query);
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
            String sql = "SELECT database, formatReadableSize(sum(data_uncompressed_bytes)) AS used_volume FROM system.parts where database = '" + dbname + "' GROUP BY database";
            resultSet = statement.executeQuery(sql);
            boolean check = false;
            while(resultSet.next()) {
                check = true;
                infoCaptureSetting.setTotalVolume(resultSet.getString("used_volume"));
            }
            if(!check) {
                infoCaptureSetting.setTotalVolume("0");
            }
            return infoCaptureSetting;
        }
        catch (Exception e) {
            String message = e.getMessage();
            e.printStackTrace();
//            if(message.contains("connect timed out")) {
            System.out.println("Error " + message);
            infoCaptureSetting.setStatus_connect(0);
            infoCaptureSetting.setMessage("Ip " + server.getIpServer() + " " + message);
            infoCaptureSetting.setIpDbLevel1(server.getIpServer());
            infoCaptureSetting.setIpDbLevel2(server.getIpServer());
            infoCaptureSetting.setIdServer(server.getId());
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
            catch (Exception e1) {
                e1.printStackTrace();
            }
            return infoCaptureSetting;
//            }
        }
//        return new InfoCaptureSetting();
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
