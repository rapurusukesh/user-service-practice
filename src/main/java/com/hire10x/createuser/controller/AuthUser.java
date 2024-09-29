package com.hire10x.createuser.controller;

import com.hire10x.createuser.model.UserLoginModel;
import com.hire10x.createuser.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthUser {


    private final UserService userService;

    public AuthUser(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/v1/login")
    public String login(@RequestBody UserLoginModel userLoginModel){
        return userService.verify(userLoginModel);
    }
}
