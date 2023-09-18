package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.dto.ResponseResult;
import com.newlife.Connect_multiple.dto.UserDto;
import com.newlife.Connect_multiple.entity.UserEntity;
import com.newlife.Connect_multiple.repository.UserRepository;
import com.newlife.Connect_multiple.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseResult findUser(UserDto userDto) {

        UserEntity user = userRepository.findByUsernameAndPassword(userDto.getUsername(), userDto.getPassword())
                .orElse(null);
//        if(user == null) {
//            return new ResponseResult("deny");
//        }
        return new ResponseResult("allow");
    }
}
