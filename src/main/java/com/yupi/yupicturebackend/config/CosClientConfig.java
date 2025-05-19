package com.yupi.yupicturebackend.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Class name: CosClientConfig
 * Package: com.yupi.yupicturebackend.config
 * Description:
 *
 * @Create: 2025/5/17 22:30
 * @Author: jay
 * @Version: 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "cos.client")
@Data
public class CosClientConfig {

    /**
     * 域名
     */
    @Value("${cos.client.host}")
    private String host;

    /**
     * secretId
     */
    @Value("${cos.client.secret-id}")
    private String secretId;

    /**
     * 密钥（注意不要泄露）
     */
    @Value("${cos.client.secret-key}")
    private String secretKey;

    /**
     * 区域
     */
    @Value("${cos.client.region}")
    private String region;

    /**
     * 桶名
     */
    @Value("${cos.client.bucket}")
    private String bucket;

    @Bean
    public COSClient cosClient() {
        if(secretId == null || secretKey == null || region == null || bucket == null) {
            throw new IllegalArgumentException("COS client configuration is incomplete");
        }
        // 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 设置bucket的区域, COS地域的简称请参照 https://www.qcloud.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        // 生成cos客户端
        return new COSClient(cred, clientConfig);
    }
}

