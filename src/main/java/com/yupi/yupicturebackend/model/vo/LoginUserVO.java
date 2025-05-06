package com.yupi.yupicturebackend.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.Date;

/**
 * Class name: LoginUserVO
 * Package: com.yupi.yupicturebackend.model.vo
 * Description:
 *
 * @Create: 2025/5/6 20:15
 * @Author: jay
 * @Version: 1.0
 */

/**
 * 脱敏后的用户信息
 */
public class LoginUserVO implements Serializable {
    /**
     * id(要指定主键策略)
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

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

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

}

