package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.api.ApiAddInfoToBroker;
import com.newlife.Connect_multiple.api.ApiCheckConnect;
import com.newlife.Connect_multiple.converter.ProbeConverter;
import com.newlife.Connect_multiple.converter.ProbeOptionConverter;
import com.newlife.Connect_multiple.dto.InfoLogin;
import com.newlife.Connect_multiple.dto.ProbeDto;
import com.newlife.Connect_multiple.dto.ProbeOptionDto;
import com.newlife.Connect_multiple.entity.*;
import com.newlife.Connect_multiple.repository.*;
import com.newlife.Connect_multiple.service.IProbeService;
import com.newlife.Connect_multiple.util.CreateTokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            if(checkProbeName(probeEntity.getName())) {
                // tên probe đã tồn tại
                responseProbe.setMessage("Name probe exists");
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
                responseProbe.setMessage(responseAddUserToBroker);
                return responseProbe;
            }

            // thêm option vào database
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
                probeEntity.setCreateAt(new Date(System.currentTimeMillis()));
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
                List<SubtopicServerEntity> listRoles = subtopicRepository.findAll();
                ServerEntity server = serverRepository.findAll().get(0);
                ProbeOptionEntity probeOptionOfServer = server.getProbeOptionEntity();
                String responseAddRuleServer = ApiAddInfoToBroker.addRuleToBroker(probeOptionOfServer.getUserName(), listRoles);
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
            List<SubtopicServerEntity> listRoles = new ArrayList<>();
            listRoles.add(subTopic);
            String responseAddRuleClient = ApiAddInfoToBroker.addRuleToBroker(probeOptionEntity.getUserName(), listRoles);
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
    @Override
    public List<ProbeDto> findAllProbe(String name, String location, String area, String vlan) {
        List<ProbeEntity> listProbe = probeRepository.findByNameOrLocationOrAreaOrVlan(name, location, area, vlan);
        List<ProbeDto> listProbeDto = new ArrayList<>();
        for(ProbeEntity entity : listProbe) {
            listProbeDto.add(ProbeConverter.toDto(entity));
        }
        return listProbeDto;
    }
    // hướng (Đưa probe vào thùng rác)
    @Override
    public String delete(Integer id) {
        try {
            ProbeEntity probeEntity = probeRepository.findByIdAndDeleted(id, 0)
                    .orElse(null);
            if(probeEntity == null) {
                return "Can not found probe with id = " + id;
            }
            ProbeHistoryEntity probeHistoryEntity = new ProbeHistoryEntity();
            probeHistoryEntity.setProbeName(probeEntity.getName());
            probeHistoryEntity.setAction("Moved to the trash");
            probeHistoryEntity.setAtTime(new Date(System.currentTimeMillis()));
            probeEntity.setDeleted(1);
            probeEntity = probeRepository.save(probeEntity);
            probeHistoryRepository.save(probeHistoryEntity);
            return "Probe with id " + id + " is moved to the trash";
        }
        catch (Exception e) {
            System.out.println("Delete probe error(Di chuyển probe tới thùng rác)");
            e.printStackTrace();
            return "Can not delete probe with " + id;
        }
    }
    // hướng
    @Override
    public String updateProbe(ProbeDto probeDto) {
        try {
            // lấy thông tin probe từ database với trạng thái vẫn hoạt động bình thường
            ProbeEntity probeEntity = probeRepository.findByIdAndDeleted(probeDto.getId(), 0)
                    .orElse(null);
            probeEntity = ProbeConverter.toEntity(probeEntity, probeDto);
            if(probeEntity == null) {
                return "Can not update probe";
            }

            // kiểm tra tính hợp lệ của địa chỉ ip của probe
            if(probeDto.getIpAddress() != null && checkValidateIpAddress(probeEntity.getIpAddress())) {
                return "Ip address invalidate";
            }
            // kiểm tra địa chỉ ip đã tồn tại trong database chưa
            if(probeDto.getIpAddress() != null) {
                Integer newId = getIdProbeByIpAddress(probeDto.getIpAddress());
                // địa chỉ ip đã tồn tại trong database và trùng với 1 probe khác
                if(newId != null && newId != probeEntity.getId()) {
                    return "IpAddress exists";
                }
            }
            // kiểm tra tên probe đã tồn tại trong database chưa
            if(probeDto.getName() != null) {
                Integer newId = getIdOfProbeByName(probeDto.getName());
                // tên probe trùng với 1 probe khác đã có trong database
                if(newId != null && newId != probeEntity.getId()) {
                    return "Name probe exists";
                }
            }
            //phục vụ cho việc yêu cầu kết nối và ngắt kết nối tới broker
            if(probeDto.getStatus() != null && probeDto.getStatus().equals("connected")) {
                Boolean checkConnectToBroker = ApiCheckConnect.checkExistClient(probeEntity.getClientId());
                if(!checkConnectToBroker) {
                    probeEntity.setStatus("error");
                    probeEntity = probeRepository.save(probeEntity);
                    return "Probe có IP là " + probeEntity.getIpAddress() + " chưa được cài đặt, không thể thực hiện connect";
                }
            }
            // lưu thông tin lịch sử khi cập nhật probe
            try {
                ProbeHistoryEntity probeHistoryEntity = new ProbeHistoryEntity();
                probeHistoryEntity.setProbeName(probeEntity.getName());
                probeHistoryEntity.setAction("Update probe");
                probeHistoryEntity.setAtTime(new Date(System.currentTimeMillis()));
                probeEntity = probeRepository.save(probeEntity);
                probeHistoryRepository.save(probeHistoryEntity);
                return "Update probe success";
            }
            catch (Exception e) {
                System.out.println("Cập nhật probe lỗi rồi!! (Line 260)");
                e.printStackTrace();
                return "Update probe failed";
            }
        }
        catch (Exception e) {
            System.out.println("Update probe error");
            e.printStackTrace();
            return "Can not update probe";
        }
    }
    // hướng
    @Override
    public String backUpProbe(Integer id) {
        try {
            // lấy probe từ database theo id và trạng thái đã đưa vào thùng rác
            ProbeEntity probeEntity = probeRepository.findByIdAndDeleted(id, 1)
                    .orElse(null);
            if(probeEntity == null) {
                return "Can not found probe with id = " + id;
            }
            ProbeHistoryEntity probeHistoryEntity = new ProbeHistoryEntity();
            probeHistoryEntity.setProbeName(probeEntity.getName());
            probeHistoryEntity.setAction("Permanently Deleted");
            probeHistoryEntity.setAtTime(new Date(System.currentTimeMillis()));
            probeEntity.setDeleted(0);
            probeEntity = probeRepository.save(probeEntity);
            probeHistoryRepository.save(probeHistoryEntity);
            return "Back up probe with id = " + id + " success";
        }
        catch (Exception e) {
            System.out.println("Back up probe error");
            e.printStackTrace();
            return "Can not back up probe with id = " + id;
        }
    }
    // hướng
    @Override
    public InfoLogin downlodFile(Integer idProbe) {
        try {
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
            info.setBrokerUrl(broker.getUrl());
            info.setKeepAlive(probeOption.getKeepAlive());
            info.setConnectionTimeOut(probeOption.getConnectionTimeOut());
            info.setClientId(probe.getClientId());
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
    @Override
    public Integer countProbeByStatus(String status) {
        try {
            Integer tmp = probeRepository.countAllByStatus(status);
            return tmp;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    // hướng - xóa 1 probe từ thùng rác
    @Override
    public String deleteProbe(Integer id) {
        // tìm probe theo id có trong thùng rác(deleted = 1)
        ProbeEntity probe = probeRepository.findByIdAndDeleted(id, 1).orElse(null);
        if(probe == null) {
            return "Probe đã bị xóa vĩnh viễn không còn tồn tại";
        }
        // thêm lịch sử vào bảng probebHistory
        try {
            ProbeHistoryEntity probeHistoryEntity = new ProbeHistoryEntity();
            probeHistoryEntity.setProbeName(probe.getName());
            probeHistoryEntity.setAction("Permanently Deleted");
            probeHistoryEntity.setAtTime(new Date(System.currentTimeMillis()));
            // xóa probe
            probeRepository.deleteById(id);
            // Lưu thông tin vào lịch sử
            probeHistoryRepository.save(probeHistoryEntity);
            return "Delete probe success";
        }
        catch (Exception e) {
            System.out.println("Lưu lịch sử probe lỗi rồi(Xóa vĩnh viễn probe từ thùng rác Line 338)");
            e.printStackTrace();
            return "Delete failed";
        }
    }
    //hướng
    private Boolean checkUsername(String username) {
        return probeOptionRepository.existsByUserName(username);
    }
    // hướng
    private Boolean checkIpAddress(String ipAddress) {
        return probeRepository.existsByIpAddress(ipAddress);
    }
    // hướng
    private Boolean checkProbeName(String probeName) {
        return probeRepository.existsByName(probeName);
    }
    // hướng
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
}
