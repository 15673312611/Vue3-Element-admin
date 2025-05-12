package com.szr.admin.common.api;

/**
 * 封装API的错误码
 */
public interface IErrorCode {
    /**
     * 获取错误码
     */
    int getCode();

    /**
     * 获取错误信息
     */
    String getMessage();
} 