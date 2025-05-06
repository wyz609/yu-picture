package com.yupi.yupicturebackend.exception;

import lombok.Getter;

/**
 * Class name: BusinessException
 * Package: com.yupi.yupicturebackend.exception
 * Description:
 *
 * @Create: 2025/4/28 23:07
 * @Author: jay
 * @Version: 1.0
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    public BusinessException(int code, String message){
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message){
        super(message);
        this.code = errorCode.getCode();
    }
}

