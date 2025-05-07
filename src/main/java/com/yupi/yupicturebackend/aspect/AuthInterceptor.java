package com.yupi.yupicturebackend.aspect;

import com.yupi.yupicturebackend.annotation.AuthCheck;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import com.yupi.yupicturebackend.model.entity.User;
import com.yupi.yupicturebackend.model.enums.UserRoleEnum;
import com.yupi.yupicturebackend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Class name: AuthInterceptor
 * Package: com.yupi.yupicturebackend.aspect
 * Description:
 *
 * @Create: 2025/5/6 21:58
 * @Author: jay
 * @Version: 1.0
 */

/**
 * 权限切面
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 环绕通知，用于在方法执行前后进行拦截处理
     * 主要用于权限检查
     *
     * @param joinPoint 切入点对象，包含被拦截的方法的信息
     * @param authCheck 注解对象，包含需要检查的权限信息
     * @return 执行结果
     * @throws Throwable 可能抛出的异常
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 获取方法上指定的角色
        String mustRole = authCheck.mustRole();
        // 获取当前请求的属性
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        // 从请求属性中获取HTTP请求对象
        HttpServletRequest reqeust = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 根据请求获取当前登录的用户信息
        User loginUser = userService.getLoginUser(reqeust);
        // 根据角色字符串获取对应的枚举对象
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        // 不需要权限，进行放行
        if(mustRoleEnum == null){
            return joinPoint.proceed();
        }
        // 以下为：必须有该权限才能通过
        // 获取当前用户具有的权限
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        // 没有权限，拒绝
        if(userRoleEnum == null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 要求必须有管理员权限，但用户没有管理员权限，所以会被拒绝
        if(UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 通过权限校验，进行放行
        return joinPoint.proceed();
    }

}

