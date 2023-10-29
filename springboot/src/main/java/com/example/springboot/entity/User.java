package com.example.springboot.entity;

import lombok.Data;

/**
 * Author：Arina
 * Date：22/10/2023 13:51
 */
@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String avatar;
    private String role;

    private String token;
}