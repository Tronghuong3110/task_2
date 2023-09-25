package com.newlife.Connect_multiple.service;

import com.newlife.Connect_multiple.dto.ResponseResult;
import com.newlife.Connect_multiple.dto.UserDto;

public interface IUserService {
    ResponseResult findUser(UserDto userDto);
}
