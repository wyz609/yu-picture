package com.yupi.yupicturebackend.exception;

import com.yupi.yupicturebackend.common.BaseResponse;
import com.yupi.yupicturebackend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Class name: GlobalExceptionHandler
 * Package: com.yupi.yupicturebackend.exception
 * Description:
 *
 * @Create: 2025/4/28 23:37
 * @Author: jay
 * @Version: 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public BaseResponse<?> businessException(BusinessException e) {
        log.error(e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    public BaseResponse<?> runtimeException(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"系统错误");
    }

}

