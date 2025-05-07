package com.yupi.yupicturebackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Class name: JsonConfgi
 * Package: com.yupi.yupicturebackend.config
 * Description:
 *
 * @Create: 2025/5/7 8:47
 * @Author: jay
 * @Version: 1.0
 */
/**
 * Spring MVC Json 配置
 */
@JsonComponent
public class JsonConfig {

    /**
 * 配置 Jackson ObjectMapper 以解决 Long 类型在转换为 JSON 时的精度丢失问题
 *
 * @param builder Jackson2ObjectMapperBuilder 构建器，用于配置和创建 ObjectMapper 实例
 * @return 配置好的 ObjectMapper 实例
 */
@Bean
public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
    // 创建 ObjectMapper 实例，不启用 XML 映射
    ObjectMapper objectMapper = builder.createXmlMapper(false).build();

    // 创建一个 SimpleModule 实例，用于注册自定义的序列化器
    SimpleModule module = new SimpleModule();

    // 为 Long 类型注册一个自定义序列化器，以避免精度丢失
    module.addSerializer(Long.class, ToStringSerializer.instance);
    // 为 long 基本类型注册一个自定义序列化器，以避免精度丢失
    module.addSerializer(Long.TYPE, ToStringSerializer.instance);

    // 注册自定义模块到 ObjectMapper
    objectMapper.registerModule(module);

    // 返回配置好的 ObjectMapper 实例
    return objectMapper;
}

}


