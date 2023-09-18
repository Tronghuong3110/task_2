package com.newlife.Connect_multiple.service.impl;

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
            probeEntity.setProbeOptionEntity(probeOptionEntity);
            // create clientId
            String clientId = System.nanoTime() + "_" + probeEntity.getName().replaceAll(" ", "_");
            // create pubtopic
            String pubtopic = probeEntity.getName().replaceAll(" ", "_") + "/" + clientId;
            probeEntity.setClientId(clientId);
            probeEntity.setPubTopic(pubtopic);
            probeEntity.setCreateAt(new Date(System.currentTimeMillis()));

            // add user to broker (success)
            if(createUserInBroker(probeOptionEntity.getUserName(), probeOptionEntity.getPassword(), clientId)) {
                // add subtopic for server
                SubtopicServerEntity subTopic = new SubtopicServerEntity();
                subTopic.setSubTopic(pubtopic);
                subtopicRepository.save(subTopic);

                // addd probe to database
                probeEntity.setStatus("disconnect");
                probeEntity = probeRepository.save(probeEntity);
                responseProbe = ProbeConverter.toDto(probeEntity);
                responseProbe.setMessage("Create probe success");

                // add record to Probe_history
                ProbeHistoryEntity probeHistoryEntity = new ProbeHistoryEntity();
                probeHistoryEntity.setAction("Create");
                probeHistoryEntity.setAtTime(new Date(System.currentTimeMillis()));
                probeHistoryEntity.setProbeName(probeEntity.getName());
                probeHistoryEntity.setIdProbe(probeEntity.getId());
                probeHistoryRepository.save(probeHistoryEntity);
            }
            return responseProbe;
        }
        catch (Exception e) {
            System.out.println("Create probe error");
            e.printStackTrace();
            responseProbe.setMessage("Create probe error");
            return responseProbe;
        }
    }

    @Override
    public List<ProbeDto> findAllProbe(String name, String location, String area, String vlan, String sortBy, Integer page) {
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, 10, sort);
        Page<ProbeEntity> listProbe = probeRepository.findByNameOrLocationOrAreaOrVlan(name, location, area, vlan, pageable);
        List<ProbeDto> listProbeDto = new ArrayList<>();
        for(ProbeEntity entity : listProbe.getContent()) {
            listProbeDto.add(ProbeConverter.toDto(entity));
        }
        return listProbeDto;
    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public String updateProbe(ProbeDto probeDto) {
        return null;
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
