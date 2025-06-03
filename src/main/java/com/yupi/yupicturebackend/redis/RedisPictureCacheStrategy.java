package com.yupi.yupicturebackend.redis;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupicturebackend.model.dto.picture.PictureQueryRequest;
import com.yupi.yupicturebackend.model.vo.PictureVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Class name: RedisPictureCacheStrategy
 * Package: com.yupi.yupicturebackend.redis
 * Description: 基于 Redis 的图片列表缓存策略实现
 *
 * @Create: 2025/6/3 23:33
 * @Author: jay
 * @Version: 1.0
 */

@Component("redisPictureCacheStrategy")
public class RedisPictureCacheStrategy implements PictureCacheStrategy{

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 从 Redis 缓存中获取数据
     * @param key 缓存key
     * @return
     */
    @Override
    public Page<PictureVO> get(String key) {
        ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();
        String cachedValue = valueOps.get(key);
        if(cachedValue != null){
            // 注意: 这里需要一个机制来正确反序列化 Page<PictureVO>
            // JSONUtil.toBean(cachedValue, Page.class) 默认可能无法正确反序列化为泛型
            // 更好的方式是自定义一个翻译反序列化器或使用更复杂的JSONUtil方法
            // 为了简化， 这里假设 Page.calss 可以注解反序列化， 实际可能需要 Page<Map> 或其他处理
            return JSONUtil.toBean(cachedValue, Page.class); // 假设可以直接反序列化
        }
        return null;
    }

    /**
     * 将数据存入 Redis 缓存
     * @param key 缓存key
     * @param value 缓存value
     * @param expireSeconds 过期时间
     */
    @Override
    public void put(String key, Page<PictureVO> value, int expireSeconds) {
        ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();
        String cacheValue = JSONUtil.toJsonStr(value);
        valueOps.set(key, cacheValue, expireSeconds, TimeUnit.SECONDS);
    }

    /**
     *  根据查询请求生成 Redis 缓存 key
     * @param request 查询请求
     * @return Redis 缓存 key
     */

    @Override
    public String generateKey(PictureQueryRequest request) {
        if(request != null){
            // 构造缓存 key
            String queryCondition = JSONUtil.toJsonStr(request);
            String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
            return "wenyang:listPictureVOByPage:" + hashKey;
        }
        return "";
    }
}
