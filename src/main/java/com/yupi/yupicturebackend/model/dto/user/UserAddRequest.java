package com.yupi.yupicturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * Class name: UserAddRequest
 * Package: com.yupi.yupicturebackend.model.dto.user
 * Description:
 *
 * @Create: 2025/5/6 23:17
 * @Author: jay
 * @Version: 1.0
 */

/**
 * 用户添加请求体
 */
@Data
public class UserAddRequest implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    private String userRole;
}


