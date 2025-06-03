package com.yupi.yupicturebackend.redis;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupicturebackend.model.dto.picture.PictureQueryRequest;
import com.yupi.yupicturebackend.model.vo.PictureVO;

/**
 * 图片列表缓存策略接口
 */

public interface PictureCacheStrategy {

    /**
     * 从缓存中获取图片VO分页数据
     * @param key 缓存key
     * @return 缓存的图片VO分页数据，如果未命中则返回null
     */
    Page<PictureVO> get(String key);


    /**
     * 将图片VO分页数据存入缓存
     * @param key 缓存key
     * @param value 图片VO分页数据
     * @param expireSeconds 过期时间（秒）
     */
    void put(String key, Page<PictureVO> value, int expireSeconds);

    /**
     * 根据查询请求生成缓存key
     * @param request 图片查询请求
     * @return 缓存过期时间(秒)
     */
    String generateKey(PictureQueryRequest request);
}

