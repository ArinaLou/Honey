package com.example.springboot.controller;


import cn.hutool.core.util.StrUtil;
import com.example.springboot.common.AuthAccess;
import com.example.springboot.common.Result;
import com.example.springboot.entity.User;
import com.example.springboot.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Author：Arina
 * Date：20/10/2023 00:29
 */
@RestController
public class WebController {

    @Resource
    UserService userService;

    @AuthAccess
    @RequestMapping("/")
    public Result hello() {
        return Result.success("success");
    }

    @PostMapping("/login")
    public Result login(@RequestBody User user){
        if(StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword())){
            return Result.error("input error");
        }
        user = userService.login(user);
        return Result.success(user);
    }

    @AuthAccess
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        if (StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword())) {
            return Result.error("Invalid data input");
        }
        if (user.getUsername().length() > 10 || user.getPassword().length() > 20) {
            return Result.error("Invalid data input");
        }
        user = userService.register(user);
        return Result.success(user);
    }


    @AuthAccess
    @PutMapping("/password")
    public Result password(@RequestBody User user) {
        if (StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPhone())) {
            return Result.error("Invalid data input");
        }
        userService.resetPassword(user);
        return Result.success();
    }

}