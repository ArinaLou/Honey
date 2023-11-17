package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.Volunteer;
import com.example.springboot.mapper.VolunteerMapper;
import org.springframework.stereotype.Service;

@Service
public class VolunteerService extends ServiceImpl<VolunteerMapper, Volunteer> {
}
