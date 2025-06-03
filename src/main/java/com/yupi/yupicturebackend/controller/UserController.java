package com.yupi.yupicturebackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.yupi.yupicturebackend.annotation.AuthCheck;
import com.yupi.yupicturebackend.common.BaseResponse;
import com.yupi.yupicturebackend.common.DeleteRequest;
import com.yupi.yupicturebackend.common.ResultUtils;
import com.yupi.yupicturebackend.constant.UserConstant;
import com.yupi.yupicturebackend.exception.ErrorCode;
import com.yupi.yupicturebackend.exception.ThrowUtils;
import com.yupi.yupicturebackend.model.dto.user.*;
import com.yupi.yupicturebackend.model.entity.User;
import com.yupi.yupicturebackend.model.vo.LoginUserVO;
import com.yupi.yupicturebackend.model.vo.UserVO;
import com.yupi.yupicturebackend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Class name: UserController
 * Package: com.yupi.yupicturebackend.controller
 * Description:
 *
 * @Create: 2025/5/6 19:38
 * @Author: jay
 * @Version: 1.0
 */

@RestController
@RequestMapping("/user")
@Api(tags = "用户登录接口")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return 返回注册结果
     */
    @PostMapping("/register")
    @ApiOperation("用户注册")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String userName = userRegisterRequest.getUserName();
        long result = userService.userRegister(userAccount, userPassword, checkPassword, userName);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求
     * @return 返回登录结果
     */
    @PostMapping("/login")
    @ApiOperation("用户登录接口")
    public BaseResponse<LoginUserVO> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    @ApiOperation("获取当前登录用户")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLonginUserVO(user));
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("用户注销")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 添加用户接口方法
     * 该方法处理用户添加请求，将请求数据转换为用户对象后进行保存
     * @param userAddRequest 用户添加请求，包含用户的基本信息
     * @return 返回一个包含用户ID的响应对象，表示添加结果
     */
    @PostMapping("/add")
    @ApiOperation("添加用户")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        // 检查请求参数是否为空，为空则抛出参数错误异常
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);

        // 创建一个新的用户对象，并将请求参数中的属性复制到用户对象中
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);

        // 设置默认密码
        final String DEFAULT_USER_PASSWORD = "12345678";
        // 对默认密码进行加密处理
        String encryptPassword = userService.getEncryptPassword(DEFAULT_USER_PASSWORD);
        // 将加密后的密码设置到用户对象中
        user.setUserPassword(encryptPassword);

        // 保存用户对象，如果保存失败则抛出操作错误异常
        boolean save = userService.save(user);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR, "添加用户失败");

        // 返回成功响应，包含新添加用户的ID
        return ResultUtils.success(user.getId());
    }

    /**
     * 根据用户ID获取用户信息
     * @param id 用户ID
     * @return 返回一个包含用户信息的响应对象
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation("获取用户信息")
    public BaseResponse<User> getUserById(long id){
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User byId = userService.getById(id);
        ThrowUtils.throwIf(byId == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(byId);
    }

    /**
     * 根据用户ID获取用户视图对象
     *
     * @param id 用户ID，用于查询用户信息
     * @return 包含用户视图对象的响应
     */
    @GetMapping("/get/vo")
    @ApiOperation("获取用户视图对象")
    public BaseResponse<UserVO> getUserVOById(long id){
        // 调用getUserById方法获取用户实体对象的响应
        BaseResponse<User> response = getUserById(id);
        // 从响应中提取用户实体对象
        User user = response.getData();
        // 使用用户实体对象调用userService的getUserVO方法获取用户视图对象，并封装到成功响应中返回
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 删除用户
     * @param deleteRequest 删除请求，包含要删除的用户ID
     * @return 返回是否删除成功的布尔值
     */
    @PostMapping("/delete")
    @ApiOperation("删除用户")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest){
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        Boolean b = userService.deleteUser(deleteRequest.getId());
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR, "删除用户失败");
        return ResultUtils.success(true);
    }

    /**
     * 更新用户信息
     * @param userUpdateRequest 用户更新请求，包含要更新的用户信息
     * @return 返回更新后的用户ID
     */
    @PostMapping("/update")
    @ApiOperation("更新用户")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(UserUpdateRequest userUpdateRequest) {
        // 检查请求参数是否为空
        ThrowUtils.throwIf(userUpdateRequest == null || userUpdateRequest.getId() == null, ErrorCode.PARAMS_ERROR);

        // 创建一个新的用户对象，并将请求参数中的属性复制到用户对象中
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);

        // 更新用户对象，如果更新失败则抛出操作错误异常
        boolean update = userService.updateById(user);
        ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "更新用户失败");

        // 返回成功响应
        return ResultUtils.success(true);
    }

    /**
     * 分页查询用户信息
     * @param userQueryRequest 用户查询请求，包含分页参数和查询条件
     * @return 返回分页的用户视图对象列表
     */
    @PostMapping("/list/page/vo")
    @ApiOperation("分页查询用户视图对象")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest){
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, pageSize), userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }
}

