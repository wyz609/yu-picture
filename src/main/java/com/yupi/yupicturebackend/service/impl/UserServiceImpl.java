package com.yupi.yupicturebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yupicturebackend.model.entity.User;
import com.yupi.yupicturebackend.service.UserService;
import com.yupi.yupicturebackend.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author jay
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-05-06 16:44:53
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




