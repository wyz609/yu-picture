package com.yupi.yupicturebackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Class name: UserLoginRequest
 * Package: com.yupi.yupicturebackend.model.dto
 * Description:
 *
 * @Create: 2025/5/6 19:57
 * @Author: jay
 * @Version: 1.0
 */

/**
 * 用户登录请求
 */
@Data
public class UserLoginRequest implements Serializable {
    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 登录账户
     */
    private String userAccount;

    /**
     * 登录密码
     */
    private String userPassword;

}

