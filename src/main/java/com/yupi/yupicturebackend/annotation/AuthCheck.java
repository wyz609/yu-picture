package com.yupi.yupicturebackend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class name: AuthCheck
 * Package: com.yupi.yupicturebackend.annotation
 * Description:
 *
 * @Create: 2025/5/6 20:58
 * @Author: jay
 * @Version: 1.0
 */

/**
 * 自定义权限注解
 */
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface AuthCheck {
    String mustRole() default ""; // 角色
}

