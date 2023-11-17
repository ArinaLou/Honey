package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author：Arina
 * Date：9/11/2023 20:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Volunteer {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private String name;
    private Integer age;
    private String phone;
    private String email;
    private String address;
    private Integer userid;
    private String date;


    @TableField(exist = false)
    private String user;

}