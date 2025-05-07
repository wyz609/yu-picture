package com.yupi.yupicturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * Class name: UserRegisterRequest
 * Package: com.yupi.yupicturebackend.model.dto
 * Description:
 *
 * @Create: 2025/5/6 17:42
 * @Author: jay
 * @Version: 1.0
 */

/**
 * 用户注册请求体
 */
@Data
public class UserRegisterRequest implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;

}

