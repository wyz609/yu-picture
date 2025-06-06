package com.yupi.yupicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupicturebackend.model.dto.space.SpaceAddRequest;
import com.yupi.yupicturebackend.model.dto.space.SpaceQueryRequest;
import com.yupi.yupicturebackend.model.entity.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupicturebackend.model.entity.User;
import com.yupi.yupicturebackend.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author jay
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-06-05 14:38:30
*/
public interface SpaceService extends IService<Space> {

    /**
     * 根据空间等级填充空间信息
     * @param space
     */
    void fillSpaceBySpaceLevel(Space space);


    /**
     * 校验请求是否合法
     * @param space 空间信息
     * @param b 是否为创建还是修改
     */
    void validSpace(Space space, boolean b);

    /**
     * 创建空间实现方法
     * @param spaceAddRequest 创建空间请求
     * @param loginUser 登录用户信息
     */
    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    /**
     * 获取空间视图类(单条)
     * @param sapce 空间信息
     * @param request 请求信息
     * @return 空间视图类
     */
    SpaceVO getSpaceVo(Space sapce, HttpServletRequest request);

    /**
     * 获取分页空空间视图类
     * @param spacePage 空间分页信息
     * @param request 请求信息
     * @return 空间分页视图类
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 获取查询条件
     * @param spaceQueryRequest 查询条件
     * @return 查询条件
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);
}
