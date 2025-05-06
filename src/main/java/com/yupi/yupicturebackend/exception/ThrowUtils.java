package com.yupi.yupicturebackend.exception;

/**
 * Class name: ThrowUtils
 * Package: com.yupi.yupicturebackend.exception
 * Description:
 *
 * @Create: 2025/4/28 23:10
 * @Author: jay
 * @Version: 1.0
 */
public class ThrowUtils {

    /**
     * 条件成立则抛出异常
     * @param condition 条件
     * @param e 异常
     */
    public static void throwIf(boolean condition, RuntimeException e) {
        if(condition){
            throw e;
        }
    }

    /**
     * 条件成立则抛出异常
     * @param condition 条件
     * @param errorCode 错误码
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition,new BusinessException(errorCode));
    }

    /**
     * 条件成立则抛出异常
     * @param condition 条件
     * @param errorCode 错误码
     * @param message 错误信息
     */
    public static void throwIf(boolean condition,ErrorCode errorCode, String message) {
        throwIf(condition,new BusinessException(errorCode,message));
    }

}

