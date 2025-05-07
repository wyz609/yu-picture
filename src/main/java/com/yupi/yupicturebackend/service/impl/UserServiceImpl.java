package com.yupi.yupicturebackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yupicturebackend.constant.UserConstant;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import com.yupi.yupicturebackend.exception.ThrowUtils;
import com.yupi.yupicturebackend.model.dto.user.UserQueryRequest;
import com.yupi.yupicturebackend.model.entity.User;
import com.yupi.yupicturebackend.model.enums.UserRoleEnum;
import com.yupi.yupicturebackend.model.vo.LoginUserVO;
import com.yupi.yupicturebackend.model.vo.UserVO;
import com.yupi.yupicturebackend.service.UserService;
import com.yupi.yupicturebackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

import java.util.*;
import java.util.stream.Collectors;

import static com.yupi.yupicturebackend.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author jay
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-05-06 16:44:53
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    /**
     * 用户注册
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 确认密码
     * @return 新用户id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1.校验
        if(StrUtil.hasBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数不能为空");
        }
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度不能小于4");
        }
        if(userPassword.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度不能小于8");
        }
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不一致");
        }
        // 2.检查是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = this.baseMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已存在");
        }
        // 3.加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 4.存入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("刘德华");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        if(!saveResult){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"注册失败");
        }
        return user.getId();
    }

    public String getEncryptPassword(String userPassword) {
        /**
         * 为原本的密码加盐
         * 盐值，混淆密码
         */
        final String SALT = "wenyang";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    /**
     * 用户登录
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.校验
        // 判断参数是否为空
        if(StrUtil.hasBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数不能为空");
        }
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度不能小于4");
        }
        if(userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度不能小于8");
        }
        // 2.加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if(user == null){
            log.info("user login failed, userAccount cannot be found");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号不存在或密码错误");
        }
        // 3.记录用户的登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE,user);
        return this.getLonginUserVO(user);
    }

    /**
     * 获取脱敏的已登录用户信息
     * @param user 用户信息
     * @return 脱敏后的用户信息
     */
    @Override
    public LoginUserVO getLonginUserVO(User user) {
        if(user == null){
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user,loginUserVO);
        return loginUserVO;
    }

    /**
     * 获取当前登录用户
     * @param request 请求
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if(currentUser == null || currentUser.getId() == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"用户未登录");
        }
        // 从数据库查询(追求性能的话可以注释，直接返回上述结果)
        Long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if(currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"用户未登录");
        }
        return currentUser;
    }

    /**
     * 用户注销
     * @param request 请求
     * @return 是否注销成功
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        // 先进行判断是否已登录
        if(userObj == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"用户未登录");
        }
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        // 移除登录态
        return true;
    }

    /**
     * 获取单个用户脱敏后的数据
     * @param user
     * @return
     */
    @Override
    public UserVO getUserVO(User user) {
        if(user == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);
        return userVO;
    }

    /**
     * 获取多个用户脱敏后的数据
     * @param userList 用户原始数据列表
     * @return 脱敏后的用户数据列表
     */
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        // 检查输入列表是否为空或null，如果是，则直接返回空列表
        if (CollUtil.isEmpty(userList)) {
            return Collections.emptyList();
        }

        // 使用流处理，过滤掉null值，并将每个用户对象映射为脱敏后的用户视图对象
        return userList.stream()
                .filter(Objects::nonNull)
                .map(this::getUserVO)
                .collect(Collectors.toList());
    }

    /**
     * 将查询条件转为QueryWrapper对象
     *
     * @param userQueryRequest 用户查询请求
     * @return QueryWrapper<User>
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        // 检查用户查询请求是否为空
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "请求参数为空");
        }

        // 获取查询条件参数
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        // 创建查询包装器对象
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        // 添加查询条件：精确匹配id
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);

        // 添加查询条件：精确匹配用户角色
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);

        // 添加查询条件：模糊匹配用户账号
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);

        // 添加查询条件：模糊匹配用户昵称
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);

        // 添加查询条件：模糊匹配用户简介
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);

        // 白名单校验排序字段
        Set<String> allowedSortFields = new HashSet<>(Arrays.asList("id", "userAccount", "userName", "userRole", "createTime"));
        if (StrUtil.isNotEmpty(sortField) && allowedSortFields.contains(sortField)) {
            boolean isAsc = StrUtil.equals(sortOrder, "ascend");
            queryWrapper.orderBy(true, isAsc, sortField);
        }
        // 返回查询包装器对象
        return queryWrapper;
    }

    /**
     * 删除用户
     * @param id 用户id
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteUser(long id) {
        // 参数校验
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 可选：添加日志记录
        log.info("Deleting user with ID: {}", id);

        // 执行删除操作并处理可能的异常
        try {
            boolean result = this.removeById(id);
            if (!result) {
                log.warn("User deletion failed for ID: {}", id);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户删除失败");
            }
            return true;
        } catch (Exception e) {
            log.error("Error occurred while deleting user with ID: {}", id, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统异常，请稍后重试");
        }
    }


}




