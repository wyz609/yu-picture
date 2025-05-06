package com.yupi.yupicturebackend.exception;

import lombok.Getter;

/**
 * Class name: ErrorCode
 * Package: com.yupi.yupicturebackend.exception
 * Description:
 *
 * @Create: 2025/4/28 22:59
 * @Author: jay
 * @Version: 1.0
 */
@Getter
public enum ErrorCode {

//    这里的自定义错误码的小技巧，当自定义错误码的时候可以跟主流的错误码(HTTP错误码)的含义保持一致，
//    当未登录的时候可以定义为"40100"在后面补两个零，和HTTP 401错误(用户需要进行身份认证)保存一致，会好理解一些

    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}

