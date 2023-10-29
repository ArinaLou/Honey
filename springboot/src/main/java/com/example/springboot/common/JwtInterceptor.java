package com.example.springboot.common;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.springboot.entity.User;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.UserMapper;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtInterceptor implements HandlerInterceptor {

    @Resource
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            token = request.getParameter("token");
        }
        // If not mapped to a method, allow access
        if (handler instanceof HandlerMethod) {
            AuthAccess annotation = ((HandlerMethod) handler).getMethodAnnotation(AuthAccess.class);
            if (annotation != null) {
                return true;
            }
        }
        // Perform authentication
        if (StrUtil.isBlank(token)) {
            throw new ServiceException("401", "Please log in");
        }
        // Get the user id from the token
        String userId;
        try {
            userId = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
            throw new ServiceException("401", "Please log in");
        }
        // Query the database based on the token's user id
        User user = userMapper.selectById(Integer.valueOf(userId));
        if (user == null) {
            throw new ServiceException("401", "Please log in");
        }
        // Verify the token with user password signature
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try {
            jwtVerifier.verify(token); // Verify the token
        } catch (JWTVerificationException e) {
            throw new ServiceException("401", "Please log in");
        }
        return true;
    }
}
