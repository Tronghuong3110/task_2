package com.newlife.Connect_multiple.service.impl;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.newlife.Connect_multiple.converter.DatabaseServerConverter;
import com.newlife.Connect_multiple.dto.DatabaseServerDto;
import com.newlife.Connect_multiple.entity.DatabaseServerMysql;
import com.newlife.Connect_multiple.entity.NasEntity;
import com.newlife.Connect_multiple.repository.DatabaseServerRepository;
import com.newlife.Connect_multiple.repository.NasRepository;
import com.newlife.Connect_multiple.service.IDatabaseService;
import com.newlife.Connect_multiple.util.ConstVariable;
import com.newlife.Connect_multiple.util.CreateTokenUtil;
import org.apache.commons.net.telnet.EchoOptionHandler;
import org.json.simple.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseService implements IDatabaseService {

    @Autowired
    private DatabaseServerRepository databaseServerRepository;
    @Autowired
    private NasRepository nasRepository;

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
            String passSudoDecode = CreateTokenUtil.deCodePass(databaseServerMysql.getPassSudo());
            response.setDbPass(passDBDecode);
            response.setSshPass(passSSHDecode);
            response.setPassSudo(passSudoDecode);
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
    @Transactional
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
            // port connect clickhouse
            if (databaseServer.getPortNumber() == null) {
                dbServer.setPortNumber(8123);
            }
            // port ssh
            if(databaseServer.getSshPort() == null) {
                dbServer.setSshPort(22);
            }
            String passDBEncode = CreateTokenUtil.enCodePass(databaseServer.getDbPass());
            String passSSHEncode = CreateTokenUtil.enCodePass(databaseServer.getSshPass());
            String passSudo = CreateTokenUtil.enCodePass(databaseServer.getPassSudo());
            dbServer.setDbPass(passDBEncode);
            dbServer.setSshPass(passSSHEncode);
            dbServer.setPassSudo(passSudo);
            dbServer.setActive(1);
            dbServer = databaseServerRepository.save(dbServer);
//            System.out.println("Pass: " + dbServer.getPassSudo() + " (line: 126)");
            JSONObject responseConfig = configNasAndDb(dbServer, databaseServer.getNasId());
            if(responseConfig.get("code").equals(0)) {
                jsonObject.put("code", 0);
                jsonObject.put("message", "Add database server fail");
                databaseServerRepository.deleteById(dbServer.getId());
                return jsonObject;
            }
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

    @Override
    public JSONObject testConnectDatabase(DatabaseServerDto server) {
        String url = "jdbc:clickhouse://" + server.getIpServer() + ":" + server.getPortNumber();
        String username = server.getDbAccount();
        String pass = server.getDbPass();
        Connection connection = null;
        JSONObject response = new JSONObject();
        try {
            connection = DriverManager.getConnection(url, username, pass);
            response.put("code", 1);
            response.put("message", "Connect database success!!");
            return response;
        }
        catch (Exception e) {
            e.printStackTrace();
            response.put("code", 0);
            response.put("message", e.getMessage());
            return response;
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public JSONObject testSSh(DatabaseServerDto server) {
        JSONObject response = new JSONObject();
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(server.getSshAccount(), server.getIpServer(), server.getSshPort()); // port mặc định là port 22
            session.setPassword(server.getSshPass());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            response.put("code", 1);
            response.put("message", "Connect ssh with ip: " + server.getIpServer() + " success!!");
            if (session.isConnected()) {
                session.disconnect();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            response.put("code", 0);
            response.put("message", "Connect ssh fail!!");
        }
        return response;
    }

    private JSONObject configNasAndDb(DatabaseServerMysql server, Integer nasId) {
        JSONObject response = new JSONObject();
        try {
            String path_file_config = ConstVariable.PATH_FILE_CONFIG;
            NasEntity nas = nasRepository.findById(nasId).orElse(null);
            System.out.println("Pass: " + server.getPassSudo() + " (line: 225)");
            if(nas == null || server == null) {
                response.put("code", 0);
                response.put("message", "Can not found server and nas");
                return response;
            }
            FileReader file = new FileReader(path_file_config + "/config_sample.yml");
            BufferedReader reader = new BufferedReader(file);
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if(line.contains("user clickhouse")) {
                    line = "    username: " + server.getDbAccount();
                }
                else if(line.contains("pass clickhouse")) {
                    line = "    password: " + "\"" + CreateTokenUtil.deCodePass(server.getDbPass()) + "\"";
                }
                else if(line.contains("ip server database")) {
                    line = "    host: " + server.getIpServer();
                }
                else if(line.contains("ip nas")) {
                    line = "    address: \"" + nas.getIp() + ":" + nas.getPort() + "\"";
                }
                else if(line.contains("user nas")) {
                    line = "    username: \"" + nas.getUsername() + "\"";
                }
                else if(line.contains("pass nas")) {
                    line = "    password: \"" + CreateTokenUtil.deCodePass(nas.getPassword()) + "\"";
                }
                else if(line.contains("path nas")) {
                    line = "    path: \"" + nas.getPath() + "\"";
                }
                output.append(line);
                output.append("\n");
            }
            FileOutputStream fileOutputStream = new FileOutputStream(path_file_config + "/config.yml");
            fileOutputStream.write(output.toString().getBytes());
            fileOutputStream.close();
            file.close();
            // copy file
            Integer status = copyFile(server);
            if(status.equals(1)) {
                response.put("code", 1);
                response.put("message", "Config success");
            }
            else {
                response.put("code", 0);
                response.put("message", "Config fail");
            }
            return response;
        }
        catch (Exception e) {
            e.printStackTrace();
            response.put("code", 0);
            response.put("message", "Can not config server");
            return response;
        }
    }

    private Integer copyFile(DatabaseServerMysql server) {
        String destinationDirectory = "/etc/clickhouse-backup/";
        String tmpPath = "/tmp";
        try {
            System.out.println("Pass: " + server.getPassSudo() + " (line: 288)");
            JSch jSch = new JSch();
            Session session = jSch.getSession(server.getSshAccount(), server.getIpServer(), server.getSshPort());
            session.setPassword(CreateTokenUtil.deCodePass(server.getSshPass()));
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            String commandCheckFileExist = "test -f " + destinationDirectory + "config.yml" + " && echo 'File exists' || echo 'File does not exist' ";
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setInputStream(null);
            channelExec.setCommand(commandCheckFileExist);
            channelExec.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));
            String output = reader.readLine();
            System.out.println("Check file exist: " + output);
            channelExec.disconnect();

            // sửa tên file nếu đã tồn tại
            String passDecode = CreateTokenUtil.deCodePass(server.getPassSudo());
            String sudoCommand = "echo " + CreateTokenUtil.deCodePass(server.getPassSudo()) + " | sudo -S ";
            System.out.println("Pass decode: " + passDecode);
            if (output.equals("File exists")) {
                // File đã tồn tại, thực hiện đổi tên file
                String renameCommand = sudoCommand + "mv " + destinationDirectory + "config.yml" + " " + destinationDirectory + "config.yml.bak." + DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss").format(LocalDateTime.now());
                channelExec = (ChannelExec) session.openChannel("exec");
                channelExec.setCommand(renameCommand);
                channelExec.setInputStream(null);
                channelExec.connect();
                System.out.println("Command rename file: " + renameCommand);
                reader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));
                String renameOutput = reader.readLine();
                System.out.println("Status rename file: " + renameOutput);
                channelExec.disconnect();
            }

            // copy file config vao server
            try {
                ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
                sftp.connect(10000);
                sftp.put(ConstVariable.PATH_FILE_CONFIG+"/config.yml", tmpPath);
                String moveFile = sudoCommand + " mv /tmp/config.yml " + destinationDirectory;
                channelExec = (ChannelExec) session.openChannel("exec");
                channelExec.setCommand(moveFile);
                channelExec.setInputStream(null);
                channelExec.connect();

                System.out.println("Remove file: " + moveFile);
                sftp.exit();
                BufferedReader reader1 = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));
                String line;
                while ((line = reader1.readLine()) != null) {
                    System.out.println("Test copy file: " + line);
                }
                return 1;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
