package com.yupi.yupicturebackend.config;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yupi.yupicturebackend.model.vo.PictureVO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Class name: CacheConfig
 * Package: com.yupi.yupicturebackend.config
 * Description: Caffeine 本地缓存配置
 *
 * @Create: 2025/6/3 23:44
 * @Author: jay
 * @Version: 1.0
 */

@Configuration
public class CacheConfig {

    /**
     * 配置用于图片VO分页数据的 Caffeine 缓存
     * @return Caffeine Cache 实例
     */
    @Bean("pictureVOCaffeineCache") // 指定 Bean 名称
    public Cache<String, Page<PictureVO>> pictureVOCaffeineCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES) // 默认写入后5分钟过期
                .maximumSize(1000) // 最大缓存条目数
                .build();
    }

}
