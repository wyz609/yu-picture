package com.yupi.yupicturebackend.redis;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.yupi.yupicturebackend.model.dto.picture.PictureQueryRequest;
import com.yupi.yupicturebackend.model.vo.PictureVO;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;

/**
 * Class name: CaffeinePictureCacheStrategy
 * Package: com.yupi.yupicturebackend.redis
 * Description: 基于 Caffeine 实现的图片列表缓存策略
 *
 * @Create: 2025/6/3 23:41
 * @Author: jay
 * @Version: 1.0
 */

@Component("caffeinePictureCacheStrategy")
public class CaffeinePictureCacheStrategy implements PictureCacheStrategy{

    // 注入 Caffeine Cache 实例， 这个实例通常在配置类中定义
    @Resource(name = "pictureVOCaffeineCache") // 确保注入的正确的Bean
    private Cache<String, Page<PictureVO>>localCache;

    /**
     * 从 Caffeine 缓存中获取数据
     */
    @Override
    public Page<PictureVO> get(String key) {
        return localCache.getIfPresent(key);
    }

    /**
     * 将数据放入 Caffeine 缓存
     * 注意：Caffeine 的过期时间通常在 Cache Bean 定义时配置。
     * 这里传入的 expireSeconds 参数在此实现中可能不会直接使用，
     * 而是依赖于 Caffeine 实例的全局过期策略。
     * 如果需要动态过期，Caffeine 也支持，但会更复杂。
     */
    @Override
    public void put(String key, Page<PictureVO> value, int expireSeconds) {
        // Caffeine 的 put 方法不直接接受 expireSeconds 参数，
        // 它的过期策略通常在构建 Cache 实例时定义。
        // 如果需要动态过期，可以使用 Caffeine 的 expireAfter(Duration) 或自定义 Expiry
        localCache.put(key, value);
    }

    /**
     * 根据查询请求生成 Caffeine 缓存 key
     */
    @Override
    public String generateKey(PictureQueryRequest request) {
        String queryCondition = JSONUtil.toJsonStr(request);
        // 本地缓存的 key 可以更精简，不用项目前缀，因为是服务器隔离的
        return "listPictureVOByPage:" + DigestUtils.md5DigestAsHex(queryCondition.getBytes());
    }
}
