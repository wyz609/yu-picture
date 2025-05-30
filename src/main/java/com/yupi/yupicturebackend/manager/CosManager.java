package com.yupi.yupicturebackend.manager;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import com.yupi.yupicturebackend.config.CosClientConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * Class name: CosManager
 * Package: com.yupi.yupicturebackend.manager
 * Description: 提供了三个方法和一个辅助方法，分别用于上传普通文件，
 *
 * @Create: 2025/5/18 14:08
 * @Author: jay
 * @Version: 1.0
 */

@Component
public class CosManager {

    // 自定义配置类，包含COS的配置信息(如存储桶名称Bucket，地域，密钥等)
    @Resource
    private CosClientConfig cosClientConfig;

    // COS客户端对象，用于与COS服务进行交互，用于执行上传、下载等操作
    @Resource
    private COSClient cosClient;


    /**
     * 上传普通文件到腾讯云COS
     *
     * @param key  文件的唯一标识(对象键),在COS中作为文件的路径和名称(如images/photo.jpg)
     * @param file 本地文件对象，表示要上传的文件
     */
    public void putObject(String key, File file){
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传图片文件到COS，并启用图片处理功能
     * @param key 图片文件的对象键
     * @param file 本地图片文件
     * @return PutObjectResult
     */
    public PutObjectResult putPictureObject(String key, File file){
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        // 对图片进行处理  （获取基本信息也被视为一种处理)
        PicOperations picOperations = new PicOperations();
        // 0 不返回原图信息， 1 返回原图信息， 默认为0
        picOperations.setIsPicInfo(1);
        // 构造处理参数
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 下载对象
     *
     * @param key 唯一键
     */
    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }


    public String getBaseUrl() {
//        return "https://wenyang-1359181609.cos.ap-guangzhou.myqcloud.com";
        // 示例实现
        return String.format("https://%s.cos.%s.myqcloud.com", cosClientConfig.getBucket(), cosClientConfig.getRegion());
    }


}
