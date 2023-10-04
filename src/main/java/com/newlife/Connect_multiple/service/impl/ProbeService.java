package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.api.ApiAddInfoToBroker;
import com.newlife.Connect_multiple.converter.ProbeConverter;
import com.newlife.Connect_multiple.converter.ProbeOptionConverter;
import com.newlife.Connect_multiple.dto.ProbeDto;
import com.newlife.Connect_multiple.dto.ProbeOptionDto;
import com.newlife.Connect_multiple.entity.*;
import com.newlife.Connect_multiple.repository.*;
import com.newlife.Connect_multiple.service.IProbeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProbeService implements IProbeService {

    @Autowired
    private ProbeRepository probeRepository;
    @Autowired
    private SubtopicOnServerRepository subtopicRepository;
    @Autowired
    private ProbeHistoryRepository probeHistoryRepository;
    @Autowired
    private ProbeOptionRepository probeOptionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ServerRepository serverRepository;

    @Override
    public ProbeDto saveProbe(ProbeDto probeDto, ProbeOptionDto probeOptionDto) {
        ProbeDto responseProbe = new ProbeDto();
        try {
            ProbeEntity probeEntity = ProbeConverter.toEntity(probeDto);
            ProbeOptionEntity probeOptionEntity = ProbeOptionConverter.toEntity(probeOptionDto);
            // check username, ip, name, clientId
            if(checkUsername(probeOptionEntity.getUserName())) {
                // username exists
                responseProbe.setMessage("Username exists");
                return responseProbe;
            }
            if(checkProbeName(probeEntity.getName())) {
                // probe name exists
                responseProbe.setMessage("Name probe exists");
                return responseProbe;
            }
            if(checkIpAddress(probeEntity.getIpAddress())) {
                // ip address exists
                responseProbe.setMessage("Ip address exists");
                return responseProbe;
            }
            if(!checkValidateIpAddress(probeEntity.getIpAddress())) {
                responseProbe.setMessage("Ip address invalidate");
            }
            probeOptionEntity = probeOptionRepository.save(probeOptionEntity);
            String responseAddUserToBroker = ApiAddInfoToBroker.addUserToBroker(probeOptionEntity.getUserName(), probeOptionEntity.getPassword());
            // TH không thêm được user vào broker
            if(!responseAddUserToBroker.equals("Create user success")) {
                responseProbe.setMessage(responseAddUserToBroker);
                return responseProbe;
            }
            probeEntity.setProbeOptionEntity(probeOptionEntity);
            // create clientId
            String clientId = System.nanoTime() + "_" + probeEntity.getName().replaceAll(" ", "_");
            // create pubtopic
            String pubtopic = probeEntity.getName().replaceAll(" ", "_") + "/" + clientId;

            // thêm probe vào database
            try {
                probeEntity.setClientId(clientId);
                probeEntity.setPubTopic(pubtopic);
                probeEntity.setCreateAt(new Date(System.currentTimeMillis()));
                probeEntity.setStatus("disconnect");
                probeEntity.setDeleted(0);
                probeEntity = probeRepository.save(probeEntity);
                responseProbe = ProbeConverter.toDto(probeEntity);
                responseProbe.setMessage("Create probe success");
            }
            catch (Exception e) {
                System.out.println("Thêm mới vào bảng probe lỗi rồi!");
                e.printStackTrace();
            }

            // thêm topic vào danh sách topic của server
            try {
                SubtopicServerEntity subTopic = new SubtopicServerEntity();
                subTopic.setSubTopic(pubtopic);
                subTopic.setIdProbe(probeEntity.getId());
                subtopicRepository.save(subTopic);
            }
            catch (Exception e) {
                System.out.println("Thêm mới vào bảng subTopic của server lỗi rồi");
                e.printStackTrace();
            }

            // lấy thông tin server từ database
            // cập nhật role để server subscribe tới topic của client
            try {
                ServerEntity server = serverRepository.findAll().get(0);
                ProbeOptionEntity probeOptionOfServer = server.getProbeOptionEntity();
                String responseAddRuleServer = ApiAddInfoToBroker.addRuleToBroker(probeOptionOfServer.getUserName(), pubtopic);
                // TH thêm role cho server lỗi
                if(!responseAddRuleServer.equals("Create rule success")) {
                    responseProbe.setMessage(responseAddRuleServer);
                    System.out.println("Thêm quyền cho server lỗi rồi!");
                    return responseProbe;
                }
            }
            catch (Exception e) {
                System.out.println("Lấy thông tin server lỗi rồi!");
                e.printStackTrace();
            }

            // Thêm quyền cho client
            String responseAddRuleClient = ApiAddInfoToBroker.addRuleToBroker(probeOptionEntity.getUserName(), pubtopic);
            // TH thêm quyền cho client lỗi
            if(!responseAddRuleClient.equals("Create rule success")) {
                responseProbe.setMessage(responseAddRuleClient);
                System.out.println("Thêm quyền subscribe tới topic cho client lỗi");
                return responseProbe;
            }
            try {
                // add record to Probe_history
                ProbeHistoryEntity probeHistoryEntity = new ProbeHistoryEntity();
                probeHistoryEntity.setAction("Create");
                probeHistoryEntity.setAtTime(new Date(System.currentTimeMillis()));
                probeHistoryEntity.setProbeName(probeEntity.getName());
                probeHistoryEntity.setProbeEntity(probeEntity);
                probeHistoryRepository.save(probeHistoryEntity);
            }
            catch (Exception e) {
                System.out.println("Thêm mới probe_history lỗi rồi!");
                e.printStackTrace();
            }
            return responseProbe;
        }
        catch (Exception e) {
            System.out.println("Tạo mới probe lỗi rồi");
            e.printStackTrace();
            responseProbe.setMessage("Create probe error");
            return responseProbe;
        }
    }

    @Override
    public List<ProbeDto> findAllProbe(String name, String location, String area, String vlan) {
        List<ProbeEntity> listProbe = probeRepository.findByNameOrLocationOrAreaOrVlan(name, location, area, vlan);
        List<ProbeDto> listProbeDto = new ArrayList<>();
        for(ProbeEntity entity : listProbe) {
            listProbeDto.add(ProbeConverter.toDto(entity));
        }
        return listProbeDto;
    }

    @Override
    public String delete(Integer id) {
        try {
            ProbeEntity probeEntity = probeRepository.findById(id)
                    .orElse(null);
            if(probeEntity == null) {
                return "Can not found probe with id = " + id;
            }
            probeEntity.setDeleted(1);
            probeEntity = probeRepository.save(probeEntity);
            return "Delete probe with id " + id + " success";
        }
        catch (Exception e) {
            System.out.println("Delete probe error");
            e.printStackTrace();
            return "Can not delete probe with " + id;
        }
    }

    @Override
    public String updateProbe(ProbeDto probeDto) {
        try {
            ProbeEntity probeEntity = probeRepository.findById(probeDto.getId())
                    .orElse(null);
            probeEntity = ProbeConverter.toEntity(probeEntity, probeDto);
            if(probeEntity == null) {
                return "Can not update probe";
            }
            if(checkValidateIpAddress(probeEntity.getIpAddress())) {
                return "Ip address invalidate";
            }
            if(checkProbeName(probeEntity.getName())) {
                return "Name probe exists";
            }
            probeEntity = probeRepository.save(probeEntity);
            return "Update probe success";
        }
        catch (Exception e) {
            System.out.println("Update probe error");
            e.printStackTrace();
            return "Can not update probe";
        }
    }

    @Override
    public String backUpProbe(Integer id) {
        try {
            ProbeEntity probeEntity = probeRepository.findById(id)
                    .orElse(null);
            if(probeEntity == null) {
                return "Can not found probe with id = " + id;
            }
            probeEntity.setDeleted(0);
            probeEntity = probeRepository.save(probeEntity);
            return "Back up probe with id = " + id + " success";
        }
        catch (Exception e) {
            System.out.println("Back up probe error");
            e.printStackTrace();
            return "Can not back up probe with id = " + id;
        }
    }

    private Boolean checkUsername(String username) {
        return probeOptionRepository.existsByUserName(username);
    }
    private Boolean checkIpAddress(String ipAddress) {
        return probeRepository.existsByIpAddress(ipAddress);
    }
    private Boolean checkProbeName(String probeName) {
        return probeRepository.existsByName(probeName);
    }
    private Boolean checkValidateIpAddress(String ipAddress) {
        String[] ips = ipAddress.split(".");
        if(ips.length < 4) return false;
        for(String ip : ips) {
            try {
                Integer subIp = Integer.parseInt(ip);
                // subIp không nằm trong vùng hợp lệ của địa chỉ ip
                if(subIp <= 0 || subIp > 255) return false;
                // subIp có chứa chữ số 0 ở đầu(001) ==> 1
                if(subIp.toString().length() != ip.length()) return false;
            }
            catch (NumberFormatException e) {
                System.out.println("Convert from subIpStr to subIpInt error");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    private Boolean createUserInBroker(String userName, String password, String clientId) {
        try {
            UserEntity user = new UserEntity();
            user.setUsername(userName);
            user.setPassword(password);
            userRepository.save(user);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
