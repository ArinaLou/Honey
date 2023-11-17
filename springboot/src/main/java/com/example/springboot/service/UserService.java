package com.example.springboot.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.User;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Resource
    UserMapper userMapper;

    @Override
    public boolean save(User entity){
        if (StrUtil.isBlank(entity.getName())){
            entity.setName(entity.getUsername());
        }
        if(StrUtil.isBlank(entity.getPassword())){
            entity.setPassword("123");
        }
        if(StrUtil.isBlank(entity.getRole())){
            entity.setRole("user");
        }
        return super.save(entity);
    }


    public User selectByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);  //  eq => ==   where username = #{username}
        return getOne(queryWrapper); //  select * from user where username = #{username}
    }


    public User login(User user) {
        User dbUser = selectByUsername(user.getUsername());
        if (dbUser == null) {

            throw new ServiceException("Incorrect username or password");
        }
        if (!user.getPassword().equals(dbUser.getPassword())) {
            throw new ServiceException("Incorrect username or password");
        }

        String token = TokenUtils.createToken(dbUser.getId().toString(), dbUser.getPassword());
        dbUser.setToken(token);
        return dbUser;
    }

    public User register(User user) {
        User dbUser = selectByUsername(user.getUsername());
        if (dbUser != null) {
            throw new ServiceException("Username already exists");
        }
        user.setName(user.getUsername());
        userMapper.insert(user);
        return user;
    }

    public void resetPassword(User user) {
        User dbUser = selectByUsername(user.getUsername());
        if (dbUser == null) {
            throw new ServiceException("User does not exist");
        }
        if (!user.getPhone().equals(dbUser.getPhone())) {
            throw new ServiceException("Verification error");
        }
        dbUser.setPassword("123");
        updateById(dbUser);
    }
}
