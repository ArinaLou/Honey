package com.example.springboot.service;

import com.example.springboot.common.Page;
import com.example.springboot.entity.User;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author：Arina
 * Date：22/10/2023 13:59
 */
@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    public void insertUser(User user){
        userMapper.insert(user);
    }

    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

    public void deleteUser(Integer id) {
        userMapper.deleteUser(id);
    }

    public void batchDeleteUser(List<Integer> ids) {
        for (Integer id : ids) {
            userMapper.deleteUser(id);  // 7  - 8
        }
    }

    public List<User> selectAll() {
        return userMapper.selectAll();
    }

    public User selectById(Integer id) {
        return userMapper.selectById(id);
    }

    public List<User> selectByName(String name) {
        return userMapper.selectByName(name);
    }

    public List<User> selectByMore(String username, String name) {
        return userMapper.selectByMore(username, name);

    }

    public List<User> selectByMo(String username, String name) {
        return userMapper.selectByMo(username, name);
    }

    public Page<User> selectByPage(Integer pageNum, Integer pageSize, String username, String name) {
        Integer skipNum = (pageNum - 1) * pageSize;  // 计算出来  1 -> 0,5    2 -> 5,5   3 -> 10,5

        Page<User> page = new Page<>();
        List<User> userList = userMapper.selectByPage(skipNum, pageSize, username, name);
        Integer total = userMapper.selectCountByPage(username, name);
        page.setTotal(total);
        page.setList(userList);
        return page;
    }

    // Validate the user account
    public User login(User user) {
        // Query user information from the database based on the username
        User dbUser = userMapper.selectByUsername(user.getUsername());
        if (dbUser == null) {
            // Throw a custom exception
            throw new ServiceException("Incorrect username or password");
        }
        if (!user.getPassword().equals(dbUser.getPassword())) {
            throw new ServiceException("Incorrect username or password");
        }
        // Generate a token
        String token = TokenUtils.createToken(dbUser.getId().toString(), dbUser.getPassword());
        dbUser.setToken(token);
        return dbUser;
    }

    public User register(User user) {
        User dbUser = userMapper.selectByUsername(user.getUsername());
        if (dbUser != null) {
            // Throw a custom exception
            throw new ServiceException("Username already exists");
        }
        user.setName(user.getUsername());
        userMapper.insert(user);
        return user;
    }

}