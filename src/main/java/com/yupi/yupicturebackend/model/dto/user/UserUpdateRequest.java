package com.yupi.yupicturebackend.model.dto.user;

/**
 * Class name: UserUpdateRequest
 * Package: com.yupi.yupicturebackend.model.dto.user
 * Description:
 *
 * @Create: 2025/5/6 23:19
 * @Author: jay
 * @Version: 1.0
 */

import lombok.Data;

/**
 * 用户更新请求体
 */
@Data
public class UserUpdateRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}

