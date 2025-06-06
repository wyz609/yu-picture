package com.yupi.yupicturebackend.manager; // 定义当前类所在的包名

import cn.hutool.core.io.FileUtil; // 导入 Hutool 工具包中的文件工具类，用于文件操作，如获取主文件名、后缀名
import com.qcloud.cos.COSClient; // 导入腾讯云 COS SDK 的核心客户端类，用于与 COS 服务进行交互
import com.qcloud.cos.exception.CosClientException; // 导入 COS 客户端异常类，用于处理 COS 操作中可能发生的客户端错误
import com.qcloud.cos.model.COSObject; // 导入 COS 对象模型，表示从 COS 获取到的文件对象
import com.qcloud.cos.model.GetObjectRequest; // 导入获取对象请求类，用于构建下载文件的请求
import com.qcloud.cos.model.PutObjectRequest; // 导入上传对象请求类，用于构建上传文件的请求
import com.qcloud.cos.model.PutObjectResult; // 导入上传对象结果类，表示上传文件到 COS 后的返回结果
import com.qcloud.cos.model.ciModel.persistence.PicOperations; // 导入 COS 图片处理操作类，用于定义图片上传时的处理规则
import com.yupi.yupicturebackend.config.CosClientConfig; // 导入自定义的 COS 客户端配置类，包含 COS 的基本配置信息
import org.springframework.beans.factory.annotation.Autowired; // 导入 Spring 的自动装配注解，用于依赖注入
import org.springframework.core.convert.ConversionService; // 导入 Spring 的类型转换服务接口 (在此代码片段中未使用)
import org.springframework.stereotype.Component; // 导入 Spring 的组件注解，标识这是一个 Spring 管理的组件

import javax.annotation.Resource; // 导入 Java EE 的资源注入注解，用于依赖注入
import java.io.File; // 导入 Java IO 的文件类，表示文件或目录路径名
import java.util.ArrayList; // 导入 Java 集合框架的 ArrayList 类，用于存储图片处理规则

/**
 * Class name: CosManager
 * Package: com.yupi.yupicturebackend.manager
 * Description: 提供了三个方法和一个辅助方法，分别用于上传普通文件，
 * 上传图片文件并进行处理，下载文件，以及获取 COS 基础 URL 和删除文件。
 * 这个类封装了与腾讯云对象存储 (COS) 交互的核心逻辑。
 *
 * @Create: 2025/5/18 14:08
 * @Author: jay
 * @Version: 1.0
 */

@Component // 标识 CosManager 类是一个 Spring 组件，由 Spring 容器进行管理和实例化
public class CosManager {

    // 自定义配置类，包含COS的配置信息(如存储桶名称Bucket，地域，密钥等)
    @Resource // Spring 注解，用于按名称或类型注入 Bean，这里注入 CosClientConfig 实例
    private CosClientConfig cosClientConfig;

    // COS客户端对象，用于与COS服务进行交互，用于执行上传、下载等操作
    @Resource // Spring 注解，注入 COSClient 实例，这是与腾讯云 COS 服务通信的核心客户端
    private COSClient cosClient;

    @Autowired // Spring 注解，用于自动装配 ConversionService 实例 (在此代码片段中未使用)
    private ConversionService conversionService;


    /**
     * 上传普通文件到腾讯云COS
     * 该方法用于将任何类型的文件（非图片，或不需图片处理的图片）上传到 COS。
     *
     * @param key  文件的唯一标识(对象键),在COS中作为文件的路径和名称(如images/photo.jpg)。
     * 这个 key 定义了文件在 COS 存储桶中的完整路径和文件名。
     * @param file 本地文件对象，表示要上传的源文件。
     */
    public void putObject(String key, File file){
        // 创建一个 PutObjectRequest 对象，用于构建上传文件的请求。
        // 参数包括：存储桶名称 (从配置中获取)、文件在 COS 中的 key、本地文件对象。
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        // 调用 COS 客户端的 putObject 方法执行文件上传操作。
        // 这个方法会阻塞直到文件上传完成。
        cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传图片文件到COS，并启用图片处理功能
     * 该方法专门用于图片上传，并可以在上传过程中对图片进行压缩、生成缩略图等处理。
     *
     * @param key 图片文件的对象键，定义图片在 COS 中的路径和名称。
     * @param file 本地图片文件，表示要上传的图片源文件。
     * @return PutObjectResult 返回上传操作的结果，其中包含图片处理后的信息。
     */
    public PutObjectResult putPictureObject(String key, File file){
        // 创建一个 PutObjectRequest 对象，用于构建上传图片文件的请求。
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);

        // 对图片进行处理，创建一个 PicOperations 对象来定义图片处理规则。
        PicOperations picOperations = new PicOperations();
        // 设置是否返回原图信息。0 表示不返回，1 表示返回原图信息。这里设置为 1，以便获取图片的原始元数据。
        picOperations.setIsPicInfo(1);
        // 创建一个 ArrayList 来存储图片处理规则。
        ArrayList<PicOperations.Rule> rules = new ArrayList<>();

        // --- 规则1: 将图片进行压缩处理 (转成 webp 格式) ---
        // 构造 WebP 格式图片在 COS 中的新对象键。
        // FileUtil.mainName(key) 获取原始 key 的主文件名（不含后缀），然后拼接 .webp。
        String webKey = FileUtil.mainName(key) + ".webp";
        // 创建一个图片处理规则对象。
        PicOperations.Rule compressRule = new PicOperations.Rule();
        // 设置图片处理规则：imageMogr2/format/webp 表示将图片转换为 WebP 格式。
        compressRule.setRule("imageMogr2/format/webp");
        // 设置处理后的图片存储的存储桶名称。
        compressRule.setBucket(cosClientConfig.getBucket());
        // 设置处理后的图片在 COS 中的新对象键（即 WebP 格式图片的存储路径和名称）。
        compressRule.setFileId(webKey);
        // 将此压缩规则添加到规则列表中。
        rules.add(compressRule);

        // --- 规则2: 缩略图处理 (仅对大于 2KB 的图片生成缩略图) ---
        // 判断本地文件大小是否大于 20KB (20 * 1024 字节)。
        if(file.length() > 20 * 1024) {
            // 创建一个图片处理规则对象，用于生成缩略图。
            PicOperations.Rule thumbnailRule = new PicOperations.Rule();
            // 设置处理后的缩略图存储的存储桶名称。
            thumbnailRule.setBucket(cosClientConfig.getBucket());
            // 构造缩略图在 COS 中的新对象键。
            // 格式为：原始主文件名_thumbnail.原始后缀名。
            String thumbnailKey = FileUtil.mainName(key) + "_thumbnail." + FileUtil.getSuffix(key);
            // 设置处理后的缩略图在 COS 中的新对象键。
            thumbnailRule.setFileId(thumbnailKey);
            // 设置缩略图规则：imageMogr2/thumbnail/128x128> 表示生成最大边长为 128x128 像素的缩略图，
            // 并且如果原图宽高小于指定值，则不进行放大处理。
            thumbnailRule.setRule(String.format("imageMogr2/thumbnail/%sx%s>", 128, 128));
            // 将此缩略图规则添加到规则列表中。
            rules.add(thumbnailRule);
        }
        // 将图片处理操作（包含所有定义的规则）设置到上传请求中。
        putObjectRequest.setPicOperations(picOperations);
        // 将规则列表设置到 PicOperations 对象中。
        picOperations.setRules(rules);
        // 调用 COS 客户端的 putObject 方法执行图片上传和处理操作，并返回结果。
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 下载对象
     * 该方法用于从腾讯云 COS 下载指定的文件对象。
     *
     * @param key 文件的唯一标识(对象键)，即要下载的文件在 COS 中的路径和名称。
     * @return COSObject 返回下载到的文件对象，其中包含文件的元数据和输入流。
     */
    public COSObject getObject(String key) {
        // 创建一个 GetObjectRequest 对象，用于构建下载文件的请求。
        // 参数包括：存储桶名称、文件在 COS 中的 key。
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        // 调用 COS 客户端的 getObject 方法执行文件下载操作，并返回 COSObject。
        return cosClient.getObject(getObjectRequest);
    }

    /**
     * 获取 COS 存储桶的基础 URL
     * 该方法用于构建 COS 存储桶的公共访问 URL，方便直接通过 HTTP 访问存储在 COS 中的文件。
     *
     * @return String 返回 COS 存储桶的基础 URL。
     */
    public String getBaseUrl() {
        // 使用 String.format 动态拼接 COS 的基础 URL。
        // 格式为：https://[Bucket名称].cos.[地域].myqcloud.com
        // 从 cosClientConfig 中获取存储桶名称和地域信息。
        return String.format("https://%s.cos.%s.myqcloud.com", cosClientConfig.getBucket(), cosClientConfig.getRegion());
    }

    /**
     * 删除对象
     * 该方法用于从腾讯云 COS 删除指定的文件对象。
     *
     * @param key 文件的唯一标识(对象键)，即要删除的文件在 COS 中的路径和名称。
     * @throws CosClientException 如果删除操作失败，则抛出 COS 客户端异常。
     */
    public void deleteObject(String key) throws CosClientException {
        // 调用 COS 客户端的 deleteObject 方法执行文件删除操作。
        // 参数包括：存储桶名称、要删除的文件在 COS 中的 key。
        cosClient.deleteObject(cosClientConfig.getBucket(), key);
    }
}
