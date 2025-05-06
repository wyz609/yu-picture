package com.yupi.yupicturebackend.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * Class name: LoggingAspect
 * Package: com.yupi.yupicturebackend.aspect
 * Description:
 *
 * @Create: 2025/4/28 22:44
 * @Author: jay
 * @Version: 1.0
 */

/**
 * 日志切面
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("execution(* com.yupi.yupicturebackend.service.*.*(..))")
    public void logBefore() {
//        System.out.println("方法执行前 - 记录日志");
        log.info("方法执行前 - 记录日志");
    }
}

