package com.yupi.yupicturebackend.model.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Class name: UserQueryRequest
 * Package: com.yupi.yupicturebackend.model.entity
 * Description:
 *
 * @Create: 2025/5/6 23:29
 * @Author: jay
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserQueryRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 当前页码
     */
    long current;

    /**
     * 每页大小
     */
    long pageSize;

    /**
     * 总数量
     */
    long total;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方式
     */
    private String sortOrder;


    private static final long serialVersionUID = 1L;
}

