package com.example.springboot.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springboot.common.AuthAccess;
import com.example.springboot.common.Result;
import com.example.springboot.entity.User;
import com.example.springboot.entity.Volunteer;
import com.example.springboot.service.UserService;
import com.example.springboot.service.VolunteerService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Author：Arina
 * Date：20/10/2023 00:29
 */
@RestController
public class WebController {

    @Resource
    UserService userService;

    @Resource
    private VolunteerService volunteerService;

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
        if (StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword()) || StrUtil.isBlank(user.getRole())) {
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

    @GetMapping("/charts")
    public Result charts() {
        // Fetch all Volunteer records
        List<Volunteer> list = volunteerService.list();

        // Extract unique dates
        Set<String> dates = list.stream().map(Volunteer::getDate).collect(Collectors.toSet());
        List<String> dateList = CollUtil.newArrayList(dates);
        dateList.sort(Comparator.naturalOrder());

        // Calculate count for each date
        List<Dict> lineList = new ArrayList<>();
        for (String date : dateList) {
            int count = (int) volunteerService.count(new QueryWrapper<Volunteer>().eq("date", date));
            Dict line = Dict.create().set("date", date).set("value", count);
            lineList.add(line);
        }

        Dict res = Dict.create().set("line", lineList);
        return Result.success(res);
    }
}