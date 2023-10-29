package com.example.springboot.exception;

import lombok.Getter;

/**
 * Author：Arina
 * Date：24/10/2023 14:15
 */
@Getter
public class ServiceException extends RuntimeException{

    private final String code;

    public ServiceException(String msg){
        super(msg);
        this.code = "500";
    }

    public ServiceException(String code, String msg){
        super(msg);
        this.code = code;
    }
}