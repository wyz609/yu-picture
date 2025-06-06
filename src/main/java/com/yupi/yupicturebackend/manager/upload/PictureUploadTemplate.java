package com.yupi.yupicturebackend.manager.upload; // 定义当前类所在的包名，表示图片上传管理模块

import cn.hutool.core.collection.CollUtil; // 导入 Hutool 工具包中的集合工具类，用于判断集合是否为空
import cn.hutool.core.date.DateUtil; // 导入 Hutool 工具包中的日期工具类，用于日期格式化
import cn.hutool.core.io.FileUtil; // 导入 Hutool 工具包中的文件工具类，用于文件操作，如获取文件后缀、主文件名、删除文件
import cn.hutool.core.util.NumberUtil; // 导入 Hutool 工具包中的数字工具类，用于数字处理，如四舍五入
import cn.hutool.core.util.RandomUtil; // 导入 Hutool 工具包中的随机工具类，用于生成随机字符串

import com.qcloud.cos.model.PutObjectResult; // 导入腾讯云 COS SDK 的上传对象结果类，包含上传后的信息
import com.qcloud.cos.model.ciModel.persistence.CIObject; // 导入 COS 图片处理结果中的 CIObject，表示处理后的图片对象信息
import com.qcloud.cos.model.ciModel.persistence.ImageInfo; // 导入 COS 图片处理结果中的 ImageInfo，表示原始图片元数据
import com.qcloud.cos.model.ciModel.persistence.ProcessResults; // 导入 COS 图片处理结果中的 ProcessResults，包含所有处理后的图片列表
import com.yupi.yupicturebackend.common.ResultUtils; // 导入自定义的结果工具类 (在此代码片段中未使用)
import com.yupi.yupicturebackend.config.CosClientConfig; // 导入自定义的 COS 客户端配置类，包含 COS 的基本配置信息
import com.yupi.yupicturebackend.exception.BusinessException; // 导入自定义的业务异常类
import com.yupi.yupicturebackend.exception.ErrorCode; // 导入自定义的错误码枚举

import com.yupi.yupicturebackend.manager.CosManager; // 导入自定义的 COS 管理器，用于与 COS 服务进行交互
import com.yupi.yupicturebackend.model.dto.file.UploadPictureResult; // 导入自定义的图片上传结果封装类 (VO/DTO)
import com.yupi.yupicturebackend.utils.PictureProcessUtils; // 导入自定义的图片处理工具类 (在此代码片段中未使用)
import lombok.extern.slf4j.Slf4j; // 导入 Lombok 的 Slf4j 注解，用于自动生成日志记录器


import javax.annotation.Resource; // 导入 Java EE 的资源注入注解，用于依赖注入
import java.io.File; // 导入 Java IO 的文件类，表示文件或目录路径名

import java.util.Date; // 导入 Java 日期类
import java.util.List; // 导入 Java 集合框架的 List 接口

/**
 * 图片上传模版抽象类，实现了模板方法模式。
 * 它定义了图片上传到腾讯云 COS 的通用流程骨架，
 * 但将具体的“校验输入源”、“获取原始文件名”和“处理文件来源”等步骤延迟到子类实现。
 */
@Slf4j // Lombok 注解，自动为类生成一个名为 'log' 的 Slf4j Logger 实例，用于记录日志
public abstract class PictureUploadTemplate {

    // 注入自定义的图片处理工具类 (在此代码片段中未使用)
    @Resource
    private PictureProcessUtils pictureProcessUtils;

    // 注入自定义的 COS 客户端配置类，用于获取 COS 相关的配置信息（如主机地址）
    @Resource
    private CosClientConfig cosClientConfig;

    // 注入自定义的 COS 管理器，用于执行实际的文件上传、下载等 COS 操作
    @Resource
    private CosManager cosManager;

    /**
     * 上传图片到腾讯云COS的核心模板方法。
     * 该方法封装了图片上传的通用步骤，包括校验、文件处理、上传、结果封装和临时文件清理。
     *
     * @param inputSource 输入源，可以是 MultipartFile（来自HTTP请求）或 URL（网络图片地址）等。
     * @param uploadPathPrefix COS 中的路径前缀，例如 "user-uploads" 或 "product-images"，用于组织 COS 中的文件结构。
     * @return UploadPictureResult 封装了上传成功后图片元数据和访问 URL 的结果对象。
     */
    public UploadPictureResult uploadPicture(Object inputSource, String uploadPathPrefix) {
        // 1. 校验图片输入源。这是一个抽象方法，具体校验逻辑由子类实现。
        validPicture(inputSource);
        // 2. 生成唯一的上传文件名和路径。
        String uuid = RandomUtil.randomString(16); // 生成一个16位的随机字符串作为文件名的一部分，增加唯一性
        String originFilename = getOriginFilename(inputSource); // 获取原始文件名，这是一个抽象方法，由子类根据 inputSource 类型实现
        // 格式化上传文件名：日期_随机字符串.原始文件后缀
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originFilename)); // 获取原始文件后缀名
        // 拼接 COS 中的完整上传路径，例如 "/product-images/2024-01-01_randomstring.jpg"
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);


        File file = null; // 声明一个 File 对象，用于表示本地临时文件
        try {
            // 3. 创建本地临时文件。
            // File.createTempFile(prefix, suffix) 会在系统临时目录创建一个唯一命名的文件。
            // 这里使用 uploadPath 作为前缀，null 作为后缀，实际后缀由系统自动生成，但后续会处理。
            file = File.createTempFile(uploadPath, null);
            // 4. 处理文件来源，将输入源的内容写入到这个本地临时文件中。这是一个抽象方法，具体处理逻辑由子类实现。
            processFile(inputSource,file);
            // 5. 上传图片到腾讯云 COS，并进行图片处理（如压缩、生成缩略图）。
            // cosManager.putPictureObject 会返回包含图片处理结果的 PutObjectResult。
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            // 从上传结果中获取原始图片的信息（如宽高、格式等）。
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // 从上传结果中获取图片处理后的结果列表。
            ProcessResults processResults = putObjectResult.getCiUploadResult().getProcessResults();
            List<CIObject> objectList = processResults.getObjectList(); // 获取处理后的图片对象列表

            // 6. 根据图片处理结果封装返回数据。
            if(CollUtil.isNotEmpty(objectList)){ // 如果图片处理结果列表不为空，说明进行了图片处理
                CIObject compressedCiobject = objectList.get(0); // 默认第一个处理结果是压缩图 (WebP)
                CIObject thumbnailCiObject = compressedCiobject; // 默认缩略图也指向压缩图

                // 只有当处理结果列表大小大于1时，才说明生成了独立的缩略图（第二个处理结果）
                if(objectList.size() > 1) {
                    thumbnailCiObject = objectList.get(1); // 获取第二个处理结果作为缩略图
                }
                // 封装包含压缩图和缩略图信息的返回结果
                return buildResult(originFilename,compressedCiobject,thumbnailCiObject);
            }
            // 如果没有图片处理结果（例如图片太小未触发缩略图，或者只上传了普通文件），则封装原始图片信息返回。
            return buildResult(originFilename, file, uploadPath, imageInfo);
        } catch (Exception e) {
            // 捕获上传过程中可能发生的任何异常
            log.error("图片上传到对象存储失败", e); // 记录详细错误日志
            // 抛出业务异常，将底层异常转换为统一的业务错误码和消息
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            // 7. 无论上传成功或失败，最后都尝试删除本地临时文件，防止资源泄露。
            this.deleteTempFile(file);
        }
    }

    /**
     * 封装返回结果，当图片未经过复杂处理（只上传原图或只获取了基本信息）时使用。
     * 使用 Builder 模式构建 UploadPictureResult 对象，提供图片元数据。
     *
     * @param originFilename 原始文件名。
     * @param file 临时文件对象，用于获取文件大小。
     * @param uploadPath COS 中的上传路径，用于构建完整的访问 URL。
     * @param imageInfo 图片元数据，包含宽度、高度、格式等。
     * @return UploadPictureResult 包含图片元数据的返回结果。
     */
    private UploadPictureResult buildResult(String originFilename, File file, String uploadPath, ImageInfo imageInfo) {
        int picWidth = imageInfo.getWidth(); // 获取图片宽度
        int picHeight = imageInfo.getHeight(); // 获取图片高度
        // 计算图片宽高比，并四舍五入保留两位小数
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();

        // 使用 Lombok 的 Builder 模式构建 UploadPictureResult 对象
        return UploadPictureResult.builder()
                .picName(FileUtil.mainName(originFilename)) // 设置图片主文件名（不含后缀）
                .picWidth(picWidth) // 设置图片宽度
                .picHeight(picHeight) // 设置图片高度
                .picScale(picScale) // 设置图片宽高比
                .picFormat(imageInfo.getFormat()) // 设置图片格式
                .picSize(FileUtil.size(file)) // 设置图片大小（从本地临时文件获取）
                .url(cosClientConfig.getHost() + "/" + uploadPath) // 设置图片的完整访问 URL
                .build(); // 构建并返回结果对象
    }

    /**
     * 封装返回结果，当图片经过 COS 图片处理（如压缩、生成缩略图）后使用。
     * 使用 Builder 模式构建 UploadPictureResult 对象，包含处理后的图片信息和缩略图信息。
     *
     * @param originFilename 原始文件名。
     * @param compressedCiObject 压缩后的图片对象，包含其在 COS 中的 key、宽高、大小、格式等。
     * @param thumbnailCiObject 缩略图对象，包含其在 COS 中的 key、宽高、大小、格式等。
     * @return UploadPictureResult 封装压缩图片对象和缩略图信息的返回结果。
     */
    private UploadPictureResult buildResult(String originFilename, CIObject compressedCiObject, CIObject thumbnailCiObject) {
        UploadPictureResult uploadPictureResult = new UploadPictureResult(); // 创建 UploadPictureResult 实例
        int picWidth = compressedCiObject.getWidth(); // 获取压缩图的宽度
        int picHeight = compressedCiObject.getHeight(); // 获取压缩图的高度
        // 计算压缩图的宽高比，并四舍五入保留两位小数
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();

        uploadPictureResult.setPicName(FileUtil.mainName(originFilename)); // 设置图片主文件名
        uploadPictureResult.setPicWidth(picWidth); // 设置压缩图宽度
        uploadPictureResult.setPicHeight(picHeight); // 设置压缩图高度
        uploadPictureResult.setPicScale(picScale); // 设置压缩图宽高比
        uploadPictureResult.setPicFormat(compressedCiObject.getFormat()); // 设置压缩图格式
        uploadPictureResult.setPicSize(compressedCiObject.getSize().longValue()); // 设置压缩图大小
        // 设置图片为压缩后的访问 URL
        uploadPictureResult.setUrl(String.format("%s/%s", cosClientConfig.getHost(), compressedCiObject.getKey()));
        // 设置缩略图的访问 URL
        uploadPictureResult.setThumbnailUrl(String.format("%s/%s", cosClientConfig.getHost(), thumbnailCiObject.getKey()));
        return uploadPictureResult; // 返回结果对象
    }


    /**
     * 抽象方法：校验输入源（本地文件或 URL）的有效性。
     * 子类需要根据不同的 inputSource 类型（如 MultipartFile, String URL）来实现具体的校验逻辑。
     * 例如，检查文件类型、大小、URL 可访问性等。
     */
    protected abstract void validPicture(Object inputSource);

    /**
     * 抽象方法：从不同的输入源中获取其原始文件名。
     * 子类需要根据 inputSource 的具体类型来提取文件名。
     * 例如，从 MultipartFile 中获取原始文件名，或从 URL 中解析文件名。
     */
    protected abstract String getOriginFilename(Object inputSource);

    /**
     * 抽象方法：处理输入源，将其内容写入到指定的本地临时文件中。
     * 子类需要根据 inputSource 的具体类型来实现文件内容的读取和写入操作。
     * 例如，从 MultipartFile 中读取流写入文件，或从 URL 下载内容写入文件。
     * @param inputSource 图片的输入源。
     * @param file 已经创建好的本地临时文件对象，内容将写入到此文件中。
     * @throws Exception 抛出处理过程中可能发生的异常。
     */
    protected abstract void processFile(Object inputSource, File file) throws Exception;


    /**
     * 删除本地临时文件。
     * 这是一个辅助方法，通常在文件上传完成后（无论成功或失败）在 finally 块中调用，
     * 以确保临时文件被清理，避免占用服务器存储空间。
     * @param file 要删除的临时文件对象。
     */
    public void deleteTempFile(File file) {
        if (file == null) { // 如果文件对象为空，则直接返回，无需删除
            return;
        }
        // 调用 File 对象的 delete() 方法尝试删除文件
        boolean deleteResult = file.delete();
        if (!deleteResult) { // 如果删除操作返回 false，表示删除失败
            // 记录错误日志，包含无法删除的文件的绝对路径
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }

}
