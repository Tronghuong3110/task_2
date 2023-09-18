package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.dto.ResponseResult;
import com.newlife.Connect_multiple.dto.UserDto;
import com.newlife.Connect_multiple.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Response;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/authentication/user/import")
    public ResponseEntity<?> createUser(UserDto userDto) {
        ResponseResult result = userService.findUser(userDto);
        return ResponseEntity.ok(result);
    }
}
