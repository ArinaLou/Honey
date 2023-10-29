package com.example.springboot.common;

import lombok.Data;

import java.util.List;

/**
 * Author：Arina
 * Date：22/10/2023 15:37
 */
@Data
public class Page<T> {
    private Integer total;
    private List<T> list;
}