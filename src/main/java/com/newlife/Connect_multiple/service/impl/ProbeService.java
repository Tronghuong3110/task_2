package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.api.ApiAddInfoToBroker;
import com.newlife.Connect_multiple.api.ApiCheckConnect;
import com.newlife.Connect_multiple.converter.ProbeConverter;
import com.newlife.Connect_multiple.converter.ProbeModuleConverter;
import com.newlife.Connect_multiple.converter.ProbeOptionConverter;
import com.newlife.Connect_multiple.dto.*;
import com.newlife.Connect_multiple.entity.*;
import com.newlife.Connect_multiple.repository.*;
import com.newlife.Connect_multiple.service.IProbeService;
import com.newlife.Connect_multiple.test.Server;
import com.newlife.Connect_multiple.util.ConstVariable;
import com.newlife.Connect_multiple.util.CreateTokenUtil;
import com.newlife.Connect_multiple.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.*;
import java.sql.Timestamp;
import java.util.*;

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
    private ServerRepository serverRepository;
    @Autowired
    private BrokerRepository brokerRepository;
    @Autowired
    private ModuleProbeRepository moduleProbeRepository;

    // hân
    @Override
    public ProbeDto findOneProbe(Integer idProbe) {
        try {
            ProbeEntity probeEntity = probeRepository.findByIdAndDeleted(idProbe, 0).orElse(null);
            if(probeEntity == null) {
                return null;
            }
            ProbeDto probeDto = ProbeConverter.toDto(probeEntity);
            return probeDto;
        } catch (Exception e){
            ProbeDto probeDto = new ProbeDto();
            return probeDto;
        }
    }

    // hướng
    @Override
    public ProbeDto saveProbe(ProbeDto probeDto, ProbeOptionDto probeOptionDto) {
        ProbeDto responseProbe = new ProbeDto();
        try {
            ProbeEntity probeEntity = ProbeConverter.toEntity(probeDto);
            ProbeOptionEntity probeOptionEntity = ProbeOptionConverter.toEntity(probeOptionDto);
            // kiểm tra username, ip, name, clientId
            if(checkUsername(probeOptionEntity.getUserName())) {
                // username đã tồn tại
                responseProbe.setMessage("Username exists");
                return responseProbe;
            }
            if(checkIpAddress(probeEntity.getIpAddress())) {
                // địa chỉ ip đã tồn tại
                responseProbe.setMessage("Ip address exists");
                return responseProbe;
            }
            if(!checkValidateIpAddress(probeEntity.getIpAddress())) {
                responseProbe.setMessage("Ip address invalidate");
            }
            String validateUsernameMessage = checkValidateUserName(probeOptionDto.getUsername());
            if(!validateUsernameMessage.equals("success")) {
                responseProbe.setMessage(validateUsernameMessage);
                return responseProbe;
            }
            // thêm username vào broker
            String responseAddUserToBroker = ApiAddInfoToBroker.addUserToBroker(probeOptionDto.getUsername(), probeOptionDto.getPassword());
            // TH không thêm được user vào broker
            if(!responseAddUserToBroker.equals("Create user success")) {
                responseProbe.setMessage("Can not add user to broker!!");
                return responseProbe;
            }

            // thêm option vào database
            probeOptionEntity.setDeleted(0);
            probeOptionEntity = probeOptionRepository.save(probeOptionEntity);
            probeEntity.setProbeOptionEntity(probeOptionEntity);

            // tạo clientId
            String clientId = System.nanoTime() + "_" + probeEntity.getName().replaceAll(" ", "_");
            // create pubtopic
            String pubtopic = probeEntity.getName().replaceAll(" ", "_") + "/" + clientId;

            // thêm probe vào database
            try {
                probeEntity.setClientId(clientId);
                probeEntity.setPubTopic(pubtopic);
                probeEntity.setCreateAt(new Timestamp(System.currentTimeMillis()));
                probeEntity.setStatus("disconnected");
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
            SubtopicServerEntity subTopic = new SubtopicServerEntity();
            try {
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
                // lấy ra danh sách toàn bộ topic của các client đã được thêm vào database
                List<SubtopicServerEntity> listSubTopic = subtopicRepository.findAll();
                ServerEntity server = serverRepository.findAll().get(0);
                ProbeOptionEntity probeOptionOfServer = server.getProbeOptionEntity();
                String responseAddRuleServer = ApiAddInfoToBroker.addRuleToBroker(probeOptionOfServer.getUserName(), listSubTopic);
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
            List<SubtopicServerEntity> listTopic = new ArrayList<>();
            // tạo ra danh sách các topic (chỉ có 1 topic của client)
            listTopic.add(subTopic);
            String responseAddRuleClient = ApiAddInfoToBroker.addRuleToBroker(probeOptionEntity.getUserName(), listTopic);
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
                probeHistoryEntity.setAtTime(new Timestamp(System.currentTimeMillis()));
                probeHistoryEntity.setProbeName(probeEntity.getName());
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
    // hướng
    // lấy ra toàn bộ probe không có trong thùng rác
    @Override
    public List<ProbeDto> findAllProbe(String name, String location, String area, String vlan) {
        List<ProbeEntity> listProbe = probeRepository.findByNameOrLocationOrAreaOrVlan(name, location, area, vlan, 0);
        List<JSONObject> countStatusOfModuleByProbe = countStatus();
        List<ProbeDto> listProbeDto = new ArrayList<>();
        for(ProbeEntity entity : listProbe) {
            ProbeDto probe = ProbeConverter.toDto(entity);
            JSONObject json = findStatusByProbe(entity.getId(), countStatusOfModuleByProbe);
            if(json != null) {
                probe.setNumberFailedModule(json.containsKey("Failed") ? json.get("Failed").toString() : "0");
                probe.setNumberPendingModule(json.containsKey("Pending") ? json.get("Pending").toString() : "0");
                probe.setNumberStopedModule(json.containsKey("Stopped") ? json.get("Stopped").toString() : "0");
                probe.setNumberRunningModule(json.containsKey("Running") ? json.get("Running").toString() : "0");
            }
            listProbeDto.add(probe);
        }
        return listProbeDto;
    }

    @Override
    public JSONObject delete(Integer id) { // hướng (Đưa probe vào thùng rác)
        JSONObject json = new JSONObject();
//        System.out.println("ID probe delete " + id);
        try {
            ProbeEntity probeEntity = probeRepository.findByIdAndDeleted(id, 0)
                    .orElse(null);
            if(probeEntity == null) {
                json.put("code", "3");
                json.put("message", "Can not found probe with id = " + id);
                return json;
            }
            ProbeOptionEntity probeOption = probeEntity.getProbeOptionEntity();
            probeOption.setDeleted(1);
            ProbeHistoryEntity probeHistoryEntity = new ProbeHistoryEntity();
            probeHistoryEntity.setProbeName(probeEntity.getName());
            probeHistoryEntity.setAction("Moved to the recycle");
            probeHistoryEntity.setAtTime(new Timestamp(System.currentTimeMillis()));
            probeEntity.setDeleted(1);
            probeEntity = probeRepository.save(probeEntity);
            probeOptionRepository.save(probeOption);
            probeHistoryRepository.save(probeHistoryEntity);
            json.put("code", "1");
            json.put("message", "Probe with id " + id + " is moved to the recycle");
            return json;
        }
        catch (Exception e) {
            System.out.println("Delete probe error(Di chuyển probe tới thùng rác)");
            e.printStackTrace();
            json.put("code", "0");
            json.put("message", "Can not remove probe with " + id);
            return json;
        }
    }
    // hướng
    @Override // cập nhật thông tin probe
    public JSONObject updateProbe(ProbeDto probeDto) {
        JSONObject json = new JSONObject();
        try {
            // lấy thông tin probe từ database với trạng thái vẫn hoạt động bình thường
            ProbeEntity probeEntity = probeRepository.findByIdAndDeleted(probeDto.getId(), 0)
                    .orElse(null);
            probeEntity = ProbeConverter.toEntity(probeEntity, probeDto);
            if(probeEntity == null) {
                json.put("code", "0");
                json.put("message", "Can not update probe");
                return json;
            }

            // kiểm tra tính hợp lệ của địa chỉ ip của probe
            if(probeDto.getIpAddress() != null && !checkValidateIpAddress(probeEntity.getIpAddress())) {
                json.put("code", "0");
                json.put("message", "Ip address invalidate");
                return json;
            }
            // kiểm tra địa chỉ ip đã tồn tại trong database chưa
            if(probeDto.getIpAddress() != null) {
                Integer newId = getIdProbeByIpAddress(probeDto.getIpAddress());
                // địa chỉ ip đã tồn tại trong database và trùng với 1 probe khác
                if(newId != null && newId != probeEntity.getId()) {
                    json.put("code", "0");
                    json.put("message", "IpAddress exists");
                    return json;
                }
            }
            // kiểm tra tên probe đã tồn tại trong database chưa
            if(probeDto.getName() != null) {
                Integer newId = getIdOfProbeByName(probeDto.getName());
                // tên probe trùng với 1 probe khác đã có trong database
                if(newId != null && newId != probeEntity.getId()) {
                    json.put("code", "3");
                    json.put("message", "Name probe exists");
                    return json;
                }
            }

            if(probeDto.getStatus() != null ) { // TH admin yêu cầu probe ngắt kết nối tới broker hoặc kết nối ới broker
                // 3 trạng thái: error, connected, disconnect
                // probeDto.getStatus().equals("connected")
                Boolean checkConnectToBroker = ApiCheckConnect.checkExistClient(probeEntity.getClientId());
//                System.out.println("Check connect " + checkConnectToBroker);
                if(!checkConnectToBroker) { // TH probe chưa chạy ứng dụng ==> không thể yêucaaufu kết nối tới broker
                    probeEntity.setStatus("error");
                    probeEntity = probeRepository.save(probeEntity);
                    json.put("code", "0");
                    json.put("message", "Probe có IP là " + probeEntity.getIpAddress() + " chưa được cài đặt, không thể thực hiện connect");
                    return json;
                }
                probeEntity.setStatus(probeDto.getStatus());
            }
            // lưu thông tin lịch sử khi cập nhật probe
            try {
                ProbeHistoryEntity probeHistoryEntity = new ProbeHistoryEntity();
                probeHistoryEntity.setProbeName(probeEntity.getName());
                probeHistoryEntity.setAction("Update probe");
                probeHistoryEntity.setAtTime(new Timestamp(System.currentTimeMillis()));
                probeEntity = probeRepository.save(probeEntity);
                probeHistoryRepository.save(probeHistoryEntity);
                System.out.println("Test update pending " + probeEntity.getPending() + " " + probeDto.getPending());
                json.put("code", "1");
                json.put("message", "Update probe success");
                return json;
            }
            catch (Exception e) {
                System.out.println("Cập nhật probe lỗi rồi!! (Line 260)");
                e.printStackTrace();
                json.put("code", "0");
                json.put("message", "Update probe failed");
                return json;
            }
        }
        catch (Exception e) {
            System.out.println("Update probe error");
            e.printStackTrace();
            json.put("code", "0");
            json.put("message", "Can not update probe");
            return json;
        }
    }
    // hướng
    @Override // khôi phục probe từ thùng rác
    public JSONArray backUpProbe(List<Integer> ids) {
        JSONArray jsonArray = new JSONArray();
        for(Integer id : ids) {
            JSONObject json = new JSONObject();
            try {
                // lấy probe từ database theo id và trạng thái đã đưa vào thùng rác
                // deleted = 1 ==> probe được đưa vào thùng rác
                ProbeEntity probeEntity = probeRepository.findByIdAndDeleted(id, 1)
                        .orElse(null);
                if(probeEntity == null) {
                    json.put("code", "3");
                    json.put("message", "Can not found probe with id = " + id);
                    jsonArray.add(json);
                    continue;
                }
                ProbeHistoryEntity probeHistoryEntity = new ProbeHistoryEntity();
                probeHistoryEntity.setProbeName(probeEntity.getName());
                probeHistoryEntity.setAction("Permanently Deleted");
                probeHistoryEntity.setAtTime(new Timestamp(System.currentTimeMillis()));
                probeEntity.setDeleted(0);
                // username, name, ip
                if(checkIpAddress(probeEntity.getIpAddress()) || checkUsername(probeEntity.getProbeOptionEntity().getUserName())) {
                    json.put("code", "0");
                    json.put("message", "Can not backup probe with name " + probeEntity.getName());
                    jsonArray.add(json);
                    continue;
                }
                ProbeOptionEntity probeOption = probeEntity.getProbeOptionEntity();
                probeOption.setDeleted(0);
                probeOptionRepository.save(probeOption);
                probeEntity = probeRepository.save(probeEntity);
                probeHistoryRepository.save(probeHistoryEntity);
                json.put("code", "1");
                json.put("message", "Back up probe with id = " + id + " success");
                jsonArray.add(json);
            }
            catch (Exception e) {
                System.out.println("Back up probe error");
                e.printStackTrace();
                json.put("code", "0");
                json.put("message", "Can not back up probe with id = " + id);
                jsonArray.add(json);
            }
        }
        return jsonArray;
    }
    // hướng
    @Override
    public InfoLogin downloadFile(Integer idProbe) {
        try {
            InetAddress ip = Inet4Address.getLocalHost();
//            System.out.println("Ip " + ip.getHostAddress());
            InfoLogin info = new InfoLogin();
            ProbeEntity probe = probeRepository.findByIdAndDeleted(idProbe, 0).orElse(null);
            if(probe == null) {
                return null;
            }
            BrokerEntity broker = new BrokerEntity();
            try {
                broker = brokerRepository.findAll().get(0);
            }
            catch (Exception e) {
                System.out.println("Lấy thông tin broker lỗi rồi!!");
                e.printStackTrace();
            }
            if(probe == null) {
                return null;
            }

            ProbeOptionEntity probeOption = probe.getProbeOptionEntity();
            String login = CreateTokenUtil.encodeToken(probeOption.getUserName(), probeOption.getPassword(), probe.getPubTopic());
            info.setLogin(login);
            info.setCleanSession(probeOption.getCleanSession());
            System.out.println("URL " + broker.getUrl());
//            info.setBrokerUrl(broker.getUrl().replaceAll("localhost", getIpAddress()));
            info.setBrokerUrl(broker.getUrl().replaceAll("localhost", ConstVariable.IPADDRESS));
            info.setKeepAlive(probeOption.getKeepAlive());
            info.setConnectionTimeOut(probeOption.getConnectionTimeOut());
            System.out.println("CLIENT ID " + probe.getClientId());
            info.setClientId(probe.getClientId());
            info.setIdProbe(probe.getId());
            return info;
        }
        catch (Exception e) {
            System.out.println("Tải file config lỗi rồi");
            e.printStackTrace();
            return null;
        }
    }
    // Han
    // màn dashboard
    @Override // xem lại
    public Integer countProbeByStatus(String status) {
        try {
            Integer tmp = probeRepository.countAllByStatusAndDeleted(status, 0);
            return tmp;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // hướng - xóa 1 probe từ thùng rác
    @Override // xóa probe từ thùng rác (xóa vĩnh viễn)
    public JSONArray deleteProbe(List<Integer> ids) {
        JSONArray jsonArray = new JSONArray();
        for(Integer id : ids) {
            JSONObject json = new JSONObject();
            // tìm probe theo id có trong thùng rác(deleted = 1)
            ProbeEntity probe = probeRepository.findByIdAndDeleted(id, 1).orElse(null);
            if(probe == null) {
                json.put("code", "3");
                json.put("message", "Probe đã bị xóa vĩnh viễn không còn tồn tại");
                jsonArray.add(json);
                continue;
            }
            // thêm lịch sử vào bảng probebHistory
            try {
                ProbeHistoryEntity probeHistoryEntity = new ProbeHistoryEntity();
                probeHistoryEntity.setProbeName(probe.getName());
                probeHistoryEntity.setAction("Permanently Deleted");
                probeHistoryEntity.setAtTime(new Timestamp(System.currentTimeMillis()));
                // xóa probe
                probeRepository.deleteById(id);
                // Lưu thông tin vào lịch sử
                probeHistoryRepository.save(probeHistoryEntity);
                json.put("code", "1");
                json.put("message", "Delete probe success");
                jsonArray.add(json);
            }
            catch (Exception e) {
                System.out.println("Lưu lịch sử probe lỗi rồi(Xóa vĩnh viễn probe từ thùng rác Line 338)");
                e.printStackTrace();
                json.put("code", "0");
                json.put("message", "Delete failed");
                jsonArray.add(json);
            }
        }
        return jsonArray;
    }

    // lấy ra toàn bộ probe phục vụ màn thùng rác
    @Override // lấy toàn bộ probe màn thùng rác
    public List<ProbeDto> findAllProbeByDeleted(String name, Integer page) {
        // lấy ra toàn bộ probe có deleted = 1(trong thùng rác)
        Pageable pageable = PageRequest.of(page, 10);
        List<ProbeEntity> listProbe = probeRepository.findByName(name, 1, pageable);
        List<ProbeDto> probeDtoList = new ArrayList<>();
        Long totalRow = probeRepository.countAllByDeleted(1, name);
        Long totalPage = Math.round(((double)totalRow) / 10);
        if(totalPage < (double)totalRow / 10) {
            totalPage += 1;
        }
//        System.out.println(("Total page " + totalPage));
        for(ProbeEntity probe : listProbe) {
            ProbeDto probeDto = ProbeConverter.toDto(probe);
            probeDto.setTotalPage(totalPage);
            probeDtoList.add(probeDto);
        }
        return probeDtoList;
    }

    @Override
    public JSONArray duplicate(DuplicateRequest duplicateRequest) {
        // Lấy ra probe mẫu trong database
        JSONArray response = new JSONArray();
        ProbeEntity probeOrigin = probeRepository.findById(duplicateRequest.getProbeOrigin()).orElse(null);
        ProbeOptionEntity probeOptionEntity = probeOrigin.getProbeOptionEntity();
        Integer idProbeOrigin = duplicateRequest.getProbeOrigin();
        String nameProbeOrigin = probeOrigin.getName();
        for(JSONObject object : duplicateRequest.getListProbe()) {
            probeOrigin.setId(null);
            JSONObject jsonObject = new JSONObject();
            String probeName = object.get("name").toString();
            String ipAddress = (String) object.get("ip");
            String clientId = System.nanoTime() + "_" + probeName.replaceAll(" ", "_");
            String topic = probeName.replaceAll(" ", "_") + "/" + clientId;
            String username = System.nanoTime() + probeName;
            if(checkIpAddress(ipAddress)) {
                // địa chỉ ip đã tồn tại
                jsonObject.put("code", 0);
                jsonObject.put("message", "Ip " + ipAddress + " address exists");
                response.add(jsonObject);
                continue;
            }
            if(!checkValidateIpAddress(ipAddress)) {
                jsonObject.put("code", 0);
                jsonObject.put("message", "Ip" + ipAddress + " invalidate");
                response.add(jsonObject);
                continue;
            }

            String responseAddUserToBroker = ApiAddInfoToBroker.addUserToBroker(username, "123456789@");
            // TH không thêm được user vào broker
            if(!responseAddUserToBroker.equals("Create user success")) {
                jsonObject.put("code", 0);
                jsonObject.put("message", "Ip " + clientId);
                response.add(jsonObject);
                continue;
            }

            // thêm option vào database
            ProbeOptionEntity probeOption = new ProbeOptionEntity();
            probeOption.setDeleted(0);
            probeOption.setUserName(username);
            probeOption.setPassword("123456789@");
            probeOption.setCleanSession(probeOptionEntity.getCleanSession());
            probeOption.setKeepAlive(probeOptionEntity.getKeepAlive());
            probeOption.setConnectionTimeOut(probeOptionEntity.getConnectionTimeOut());
            probeOption = probeOptionRepository.save(probeOption);

            probeOrigin.setStatus("disconnected");
            probeOrigin.setDeleted(0);
            probeOrigin.setNumberFailedModule(0);
            probeOrigin.setNumberRunningModule(0);
            probeOrigin.setNumberPendingModule(0);
            probeOrigin.setPubTopic(topic);
            probeOrigin.setClientId(clientId);
            probeOrigin.setName(probeName);
            probeOrigin.setIpAddress(ipAddress);
            probeOrigin.setProbeOptionEntity(probeOption);
            probeOrigin.setPending(false);
            probeOrigin.setCreateAt(new Timestamp(System.currentTimeMillis()));
//            probeOrigin = ProbeConverter.toEntity(probeOrigin);
            probeOrigin = probeRepository.save(probeOrigin);

            // thêm topic vào danh sách topic của server
            SubtopicServerEntity subTopic = new SubtopicServerEntity();
            try {
                subTopic.setSubTopic(topic);
                subTopic.setIdProbe(probeOrigin.getId());
                subTopic = subtopicRepository.save(subTopic);
            }
            catch (Exception e) {
                System.out.println("Thêm mới vào bảng subTopic của server lỗi rồi (Duplicate)");
                e.printStackTrace();
            }

            // lấy thông tin server từ database
            // cập nhật role để server subscribe tới topic của client
            try {
                // lấy ra danh sách toàn bộ topic của các client đã được thêm vào database
                List<SubtopicServerEntity> listSubTopic = subtopicRepository.findAll();
                ServerEntity server = serverRepository.findAll().get(0);
                ProbeOptionEntity probeOptionOfServer = server.getProbeOptionEntity();
                String responseAddRuleServer = ApiAddInfoToBroker.addRuleToBroker(probeOptionOfServer.getUserName(), listSubTopic);
                // TH thêm role cho server lỗi
                if(!responseAddRuleServer.equals("Create rule success")) {
                    System.out.println("Thêm quyền cho server lỗi rồi! (duplicate)");
                    probeRepository.deleteById(probeOrigin.getId());
                    probeOptionRepository.deleteById(probeOptionEntity.getId());
                    jsonObject.put("code", 0);
                    jsonObject.put("message", "Server can not subscribe to topic of probe have ip " + ipAddress);
                    response.add(jsonObject);
                    continue;
                }
            }
            catch (Exception e) {
                System.out.println("Lấy thông tin server lỗi rồi!");
                e.printStackTrace();
            }

            // Thêm quyền cho client
            List<SubtopicServerEntity> listTopic = new ArrayList<>();
            // tạo ra danh sách các topic (chỉ có 1 topic của client)
            listTopic.add(subTopic);
            String responseAddRuleClient = ApiAddInfoToBroker.addRuleToBroker(username, listTopic);
            // TH thêm quyền cho client lỗi
            if(!responseAddRuleClient.equals("Create rule success")) {
                System.out.println("Thêm quyền subscribe tới topic cho client lỗi!! (duplicate)");
                probeRepository.deleteById(probeOrigin.getId());
                probeOptionRepository.deleteById(probeOptionEntity.getId());
                jsonObject.put("code", 0);
                jsonObject.put("message", "Ip " + ipAddress + " can not subscribe to topic");
                response.add(jsonObject);
                continue;
            }
            try {
                // add record to Probe_history
                ProbeHistoryEntity probeHistoryEntity = new ProbeHistoryEntity();
                probeHistoryEntity.setAction("Cloned from probe " + nameProbeOrigin);
                probeHistoryEntity.setAtTime(new Timestamp(System.currentTimeMillis()));
                probeHistoryEntity.setProbeName(probeName);
                probeHistoryRepository.save(probeHistoryEntity);
            }
            catch (Exception e) {
                System.out.println("Thêm mới probe_history lỗi rồi!");
                e.printStackTrace();
            }

            jsonObject.put("code", 1);
            jsonObject.put("message", "Duplicate probe with ip " + ipAddress + " success");
            response.add(jsonObject);

            List<ProbeModuleEntity> listModule = moduleProbeRepository.findAllModuleByProbe(idProbeOrigin);
            for(ProbeModuleEntity probeModuleEntity : listModule) {
                probeModuleEntity.setId(null);
                probeModuleEntity.setErrorPerWeek(0);
                probeModuleEntity.setStatus("Stopped");
                probeModuleEntity.setLoading(0);
                probeModuleEntity.setExpectStatus(0);
                probeModuleEntity.setProcessStatus(2);
                probeModuleEntity.setProcessId("0");
                probeModuleEntity.setIdProbe(probeOrigin.getId());
                probeModuleEntity = ProbeModuleConverter.toEntity(probeModuleEntity);
                moduleProbeRepository.save(probeModuleEntity);
            }
            probeOrigin.setNumberStopedModule(listModule.size());
            probeOrigin.setTotalModule(listModule.size());
            probeRepository.save(probeOrigin);
        }
        return response;
    }

    // đếm số lượng module theo probe và status
    private List<JSONObject> countStatus() {
        List<JSONObject> result = probeRepository.countStatusByProbe();
        return result;
    }
    // tìm
    private JSONObject findStatusByProbe(Integer id, List<JSONObject> listStatusOfProbe) {
        for(JSONObject json : listStatusOfProbe) {
            Integer idProbe = Integer.parseInt(json.get("id_probe").toString());
            if (id.equals(idProbe)) {
                JSONObject status = JsonUtil.parseJson(json.get("status_counts").toString());
                return status;
            }
        }
        return null;
    }
    //hướng
    private Boolean checkUsername(String username) {
        return probeOptionRepository.existsByUserNameAndDeleted(username, 0);
    }
    // hướng
    private Boolean checkIpAddress(String ipAddress) {
        return probeRepository.existsByIpAddressAndDeleted(ipAddress, 0);
    }
    // hướng
    private Boolean checkValidateIpAddress(String ipAddress) {
        String[] ips = ipAddress.split("\\.");
        System.out.println("Len " +  ips.length + " " + ipAddress);
        if(ips.length != 4) {
            System.out.println(0);
            return false;
        }
        for(String ip : ips) {
            try {
                Integer subIp = Integer.parseInt(ip);
                System.out.println("Sub IP " + subIp);
                // subIp không nằm trong vùng hợp lệ của địa chỉ ip
                if(subIp <= 0 || subIp > 255) {
                    System.out.println(1);
                    return false;
                }
                // subIp có chứa chữ số 0 ở đầu(001) ==> 1
                if(subIp.toString().length() != ip.length()) {
                    System.out.println(2);
                    return false;
                }
            }
            catch (NumberFormatException e) {
                System.out.println("Convert from subIpStr to subIpInt error");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    // hướng
    private String checkValidateUserName(String userName) {
        String[] usernames = userName.split(" ");
        if(usernames.length >= 2) {
            return "Username không được chứa khoảng trắng";
        }
        boolean containsUTF8 = StringUtils.contains(userName, '\u0080');
        if(containsUTF8) {
            return "Username không được chứa ký tự UTF-8";
        }
        return "success";
    }
    //hướng
    private Integer getIdOfProbeByName(String name) {
        ProbeEntity probe = probeRepository.findByName(name).orElse(null);
        if(probe == null) {
            return null;
        }
        return probe.getId();
    }
    // hướng
    private Integer getIdProbeByIpAddress(String ipAddress) {
        ProbeEntity probe = probeRepository.findByIpAddress(ipAddress).orElse(null);
        if(probe == null) {
            return null;
        }
        return probe.getId();
    }

    private String getIpAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            for(NetworkInterface net : Collections.list(networkInterfaceEnumeration)) {
                Enumeration<InetAddress> inetAddresses = net.getInetAddresses();
                for(InetAddress ipAddress : Collections.list(inetAddresses)) {
                    if(ipAddress.isSiteLocalAddress() && net.getDisplayName().startsWith("Intel(R) Wireless")) {
                        System.out.println("Name " + net.getDisplayName());
                        System.out.println("IP local " + ipAddress.getHostAddress());
                        return ipAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public String saveServer(ServerEntity server, ProbeOptionEntity optionEntity) {
        try {
            // Thêm probeOption vaào database
            ProbeOptionEntity probeOptionEntity = probeOptionRepository.findByUserName(optionEntity.getUserName()).orElse(null);
            List<ServerEntity> serverEntity = serverRepository.findAll();
            if(probeOptionEntity != null || serverEntity.size() > 0) {
                return "Đã có server!!";
            }
            // TH chưa có server ==> add server vào broker ==> add rule cho server ==> lưu option của server
            String addServerToBroker = ApiAddInfoToBroker.addUserToBroker(optionEntity.getUserName(), optionEntity.getPassword());
            if(!addServerToBroker.equals("Create user success")) {
                return "Thêm mới server lỗi rồi";
            }
            List<SubtopicServerEntity> listTopic = subtopicRepository.findAll();
            if (listTopic.size() > 0) {
                String addRule = ApiAddInfoToBroker.addRuleToBroker(optionEntity.getUserName(), listTopic);
                if(!addRule.equals("Create rule success")) {
                    return "Thêm rule cho server lỗi rồi";
                }
            }
            // Sau đó lưu server
            optionEntity = probeOptionRepository.save(optionEntity);
            server.setProbeOptionEntity(optionEntity);
            serverRepository.save(server);
            // lưu thông tin broker
            BrokerEntity broker = new BrokerEntity();
            broker.setUrl("tcp://localhost:1883");
            broker.setPassword("1234");
            broker.setUsername("admin");
            broker = brokerRepository.save(broker);
            return "Thêm mới server thành công";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Thêm lỗi rồi!!";
        }
    }

    public void checkConnectFromProbeToBroker() {
        try {
            List<ProbeEntity> listProbeIsConnected = probeRepository.findProbeByDeletedAndStatus(0, "connected");
            for(ProbeEntity probeEntity : listProbeIsConnected) {
                Boolean checkConnect = ApiCheckConnect.checkExistClient(probeEntity.getClientId());
                if(!checkConnect) {
                    probeEntity.setStatus("error");
                }
            }
            probeRepository.saveAll(listProbeIsConnected);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Kiểm tra trạng thái probe với broker lỗi rồi!!");
        }
    }
}
