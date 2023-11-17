package com.example.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.User;
import com.example.springboot.entity.Volunteer;
import com.example.springboot.service.UserService;
import com.example.springboot.service.VolunteerService;
import com.example.springboot.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author：Arina
 * Date：9/11/2023 20:33
 */
@RestController
@RequestMapping("/volunteer")
public class VolunteerController {
    
    @Autowired
    VolunteerService volunteerservice;

    @Autowired
    UserService userService;

    /**
     * 新增信息
     */
    @PostMapping("/add")
    public Result add(@RequestBody Volunteer Volunteer) {
        User currentUser = TokenUtils.getCurrentUser();  // 获取到当前登录的用户信息
        Volunteer.setUserid(currentUser.getId());
        Volunteer.setDate(DateUtil.today());  //   2023-10-08
        volunteerservice.save(Volunteer);
        return Result.success();
    }

    /**
     * 修改信息
     */
    @PutMapping("/update")
    public Result update(@RequestBody Volunteer Volunteer) {
        volunteerservice.updateById(Volunteer);
        return Result.success();
    }

    /**
     * 删除信息
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        volunteerservice.removeById(id);
        return Result.success();
    }


    /**
     * 批量删除信息
     */
    @DeleteMapping("/delete/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        volunteerservice.removeBatchByIds(ids);
        return Result.success();
    }

    /**
     * 查询全部信息
     */
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<Volunteer> VolunteerList = volunteerservice.list(new QueryWrapper<Volunteer>().orderByDesc("id"));
        return Result.success(VolunteerList);
    }

    /**
     * 根据ID查询信息
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Volunteer Volunteer = volunteerservice.getById(id);
        User user = userService.getById(Volunteer.getId());
        if (user != null) {
            Volunteer.setUser(user.getName());
        }
        return Result.success(Volunteer);
    }


    /**
     * 多条件模糊查询信息
     * pageNum 当前的页码
     * pageSize 每页查询的个数
     */
    @GetMapping("/selectByPage")
    public Result selectByPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam String name) {
        QueryWrapper<Volunteer> queryWrapper = new QueryWrapper<Volunteer>().orderByDesc("id");  // 默认倒序，让最新的数据在最上面
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        Page<Volunteer> page = volunteerservice.page(new Page<>(pageNum, pageSize), queryWrapper);
        List<Volunteer> records = page.getRecords();
        for (Volunteer record : records) {
            Integer authorid = record.getUserid();
            User user = userService.getById(authorid);
            if (user != null) {
                record.setUser(user.getName());
            }
        }
        return Result.success(page);
    }


}

