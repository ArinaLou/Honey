package com.example.springboot.controller;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.User;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.service.UserService;
import com.example.springboot.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    // Add user information
    @PostMapping("/add")
    public Result add(@RequestBody User user){
        try{
            userService.save(user);
        }catch (Exception e){
            if (e instanceof DuplicateKeyException){
                return Result.error("Duplicate data insertion");
            }else {
                return Result.error("System error");
            }
        }
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody User user){
        userService.updateById(user);
        return Result.success();
    }

    /**
     * Delete user information
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        User currentUser = TokenUtils.getCurrentUser();
        if (id.equals(currentUser.getId())) {
            throw new ServiceException("You cannot delete the current user");
        }
        userService.removeById(id);
        return Result.success();
    }

    /**
     * Batch delete user information
     */
    @DeleteMapping("/delete/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {  //  [7, 8]
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser != null && currentUser.getId() != null && ids.contains(currentUser.getId())) {
            throw new ServiceException("You cannot delete the current user");
        }
        userService.removeBatchByIds(ids);
        return Result.success();
    }

    /**
     * Retrieve all user information
     */
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<User> userList;
        userList = userService.list(new QueryWrapper<User>().orderByDesc("id"));
        return Result.success(userList);
    }

    /**
     * Retrieve user information by ID
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        User user = userService.getById(id);
        return Result.success(user);
    }


    /**
     * Fuzzy search for user information based on multiple conditions
     * pageNum: current page number
     * pageSize: number of records per page
     */
    @GetMapping("/selectByPage")
    public Result selectByPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam String username,
                               @RequestParam String name) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>().orderByDesc("id");
        queryWrapper.like(StrUtil.isNotBlank(username), "username", username);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        // select * from user where username like '%#{username}%' and name like '%#{name}%'
        Page<User> page = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.success(page);
    }
}
