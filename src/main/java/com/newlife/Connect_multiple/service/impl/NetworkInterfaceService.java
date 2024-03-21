package com.newlife.Connect_multiple.service.impl;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.newlife.Connect_multiple.dto.NetworkInterfaceDto;
import com.newlife.Connect_multiple.entity.NetworkInterfaceEntity;
import com.newlife.Connect_multiple.entity.ProbeEntity;
import com.newlife.Connect_multiple.repository.INetworkInterfaceRepository;
import com.newlife.Connect_multiple.repository.ProbeRepository;
import com.newlife.Connect_multiple.service.INetworkInterfaceService;
import com.newlife.Connect_multiple.util.CreateTokenUtil;
import org.apache.commons.compress.utils.CharsetNames;
import org.json.simple.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class NetworkInterfaceService implements INetworkInterfaceService {
    @Autowired
    private INetworkInterfaceRepository networkInterfaceRepository;
    @Autowired
    private ProbeRepository probeRepository;

    @Override
    public List<NetworkInterfaceDto> findAllByProbe(Integer idProbe) {
        try {
            List<NetworkInterfaceEntity> interfaces = networkInterfaceRepository.findAllByIdProbe(idProbe);
            List<NetworkInterfaceDto> listResponse = new ArrayList<>();
            for(NetworkInterfaceEntity networkInterface : interfaces) {
                NetworkInterfaceDto networkInterfaceDto = new NetworkInterfaceDto();
                BeanUtils.copyProperties(networkInterface, networkInterfaceDto);
                listResponse.add(networkInterfaceDto);
            }
            return listResponse;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject update(NetworkInterfaceDto networkInterfaceDto) {
        JSONObject response = new JSONObject();
        try {
            NetworkInterfaceEntity networkInterface = networkInterfaceRepository.findById(networkInterfaceDto.getId()).orElse(null);
            if(networkInterface == null) {
                response.put("code", 0);
                response.put("message", "Can not found interface with id = " + networkInterface.getId());
                return response;
            }
            if(networkInterfaceDto.getMonitor() != null) {
                networkInterface.setMonitor(networkInterfaceDto.getMonitor());
            }
            if(networkInterfaceDto.getDescription() != null) {
                // update trong probe
                String message = updateDescription(networkInterfaceDto.getIdProbe(), networkInterfaceDto.getDescription(), networkInterface.getDescription());
                if(message.equals("success")) {
                    System.out.println("Update description success!!");
                    networkInterface.setDescription(networkInterface.getDescription());
                }
                // TH update không thanh công
                else {
                    response.put("code", 0);
                    response.put("message", "Can not update description of probe");
                    return response;
                }
            }
            networkInterfaceRepository.save(networkInterface);
            response.put("code", 1);
            response.put("message", "success");
        }
        catch (Exception e) {
            e.printStackTrace();
            response.put("code", 0);
            response.put("message", "Update interface fail");
        }
        return response;
    }

    private String updateDescription(Integer idProbe, String newDescription, String oldDescription) {
        try {
            ProbeEntity probe = probeRepository.findByIdAndDeleted(idProbe, 0).orElse(null);
            if(probe == null) {
                return "Can not found probe by id = " + idProbe;
            }
            JSch jSch = new JSch();
            Session session = jSch.getSession(probe.getSshAccount(), probe.getIpAddress(), probe.getSshPort());
            session.setPassword(CreateTokenUtil.deCodePass(probe.getSshPass()));
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            String sudoCommand = "echo " + CreateTokenUtil.deCodePass(probe.getSudoPass()) + " | sudo -S ";
            String command = sudoCommand + "nmcli con modify '" + oldDescription + "' connect.id '" + newDescription + "'";
            System.out.println("Command change description: " + command);
            channelExec.setCommand(command);
            channelExec.setInputStream(null);
            channelExec.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Output: " + line);
            }
            channelExec.disconnect();
            session.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }
}
