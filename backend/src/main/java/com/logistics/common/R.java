package com.logistics.common;

import lombok.Data;

/**
 * 通用响应类
 */
@Data
public class R<T> {
    
    private Integer code;
    private String message;
    private T data;
    private boolean success;
    
    /**
     * 成功响应
     */
    public static <T> R<T> success(T data) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage("操作成功");
        r.setData(data);
        r.setSuccess(true);
        return r;
    }
    
    /**
     * 成功响应，自定义消息
     */
    public static <T> R<T> success(String message, T data) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage(message);
        r.setData(data);
        r.setSuccess(true);
        return r;
    }
    
    /**
     * 失败响应
     */
    public static <T> R<T> error(String message) {
        R<T> r = new R<>();
        r.setCode(500);
        r.setMessage(message);
        r.setSuccess(false);
        return r;
    }
    
    /**
     * 失败响应，自定义错误码
     */
    public static <T> R<T> error(Integer code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        r.setSuccess(false);
        return r;
    }
} 