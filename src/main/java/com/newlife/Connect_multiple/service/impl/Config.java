package com.newlife.Connect_multiple.service.impl;

import com.jcraft.jsch.*;
import com.newlife.Connect_multiple.entity.DatabaseServerMysql;
import com.newlife.Connect_multiple.entity.NasEntity;
import com.newlife.Connect_multiple.repository.DatabaseServerRepository;
import com.newlife.Connect_multiple.repository.NasRepository;
import com.newlife.Connect_multiple.service.IConfigService;
import com.newlife.Connect_multiple.util.ConstVariable;
import com.newlife.Connect_multiple.util.CreateTokenUtil;
import expectj.ExpectJ;
import expectj.Spawn;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class Config implements IConfigService {
    @Autowired
    private NasRepository nasRepository;
    @Autowired
    private DatabaseServerRepository databaseServerRepository;

    @Override
    public JSONObject configNasAndDb(Integer serverId, Integer nasId) {
        JSONObject response = new JSONObject();
        try {
            String path_file_config = ConstVariable.PATH_FILE_CONFIG;
            NasEntity nas = nasRepository.findById(nasId).orElse(null);
            DatabaseServerMysql server = databaseServerRepository.findById(serverId).orElse(null);
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
        try {
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
            String sudoCommand = "echo " + CreateTokenUtil.deCodePass(server.getPassSudo()) + " | sudo -S ";
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
                sftp.put(ConstVariable.PATH_FILE_CONFIG+"/config.yml", destinationDirectory);
                sftp.exit();
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
