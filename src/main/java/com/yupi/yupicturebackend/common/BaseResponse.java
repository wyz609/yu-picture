package com.yupi.yupicturebackend.common;

import com.yupi.yupicturebackend.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * Class name: BaseResponse
 * Package: com.yupi.yupicturebackend.common
 * Description:
 *
 * @Create: 2025/4/28 23:22
 * @Author: jay
 * @Version: 1.0
 */

@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code,T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code,data,"");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(),null,errorCode.getMessage());
    }
}

