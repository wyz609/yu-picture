package com.yupi.yupicturebackend.utils;

import com.luciad.imageio.webp.WebPWriteParam;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Class name: PictureProcessUtils
 * Package: com.yupi.yupicturebackend.utils
 * Description: 该工具类用来处理图片，将原始图片进行完成压缩和预览
 *
 * @Create: 2025/6/4 23:15
 * @Author: jay
 * @Version: 1.0
 */

@Component
@Slf4j
public class PictureProcessUtils {

    /**
     * 转换缩略图
     * - 格式：256 * 256
     * - 如果原图宽高小于设定值，不修改
     * - （Thumbnails的缩略宽高小于设定值默认好像是不会修改的，为了清晰一点，自己又添加一个判断逻辑）
     *
     * @param originalImage 原图
     */
    public void toThumbnailImage(File originalImage, File thumbnailFile) {

        try {
            // 获取原图的宽高
            BufferedImage image = ImageIO.read(originalImage);
            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();

            // 设置目标尺寸
            int targetWidth = 256;
            int targetHeight = 256;

            // 如果目标尺寸大于原图的尺寸，不进行缩放
            if (targetWidth > originalWidth || targetHeight > originalHeight) {
                targetWidth = originalWidth;
                targetHeight = originalHeight;
            }
            // 生成缩略图
            Thumbnails.of(originalImage)
                    .size(targetWidth, targetHeight)
                    .toFile(thumbnailFile);

        } catch (IOException e) {
            log.error("生成缩略图失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成缩略图是失败");
        }
    }

    /**
     * 转换为压缩图（预览图）
     * - webp 格式
     *
     * @param originalImage 原图
     */
    public void toPreviewImage(File originalImage, File previewImage) {
        try {
            // 获取原始文件的编码
            BufferedImage image = ImageIO.read(originalImage);
            // 创建WebP ImageWriter实例
            ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();
            // 配置编码参数
            WebPWriteParam writeParam = new WebPWriteParam(writer.getLocale());
            // 设置压缩模式
            writeParam.setCompressionMode(WebPWriteParam.MODE_DEFAULT);
            // 配置ImageWriter输出
            writer.setOutput(new FileImageOutputStream(previewImage));
            // 进行编码，重新生成新图片
            writer.write(null, new IIOImage(image, null, null), writeParam);
        } catch (IOException e) {
            log.error("生成压缩图（预览图）失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成压缩图（预览图）失败");
        }
    }
}
