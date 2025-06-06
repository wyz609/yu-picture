package com.yupi.yupicturebackend.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * Class name: SpaceUpateRequest
 * Package: com.yupi.yupicturebackend.model.dto.space
 * Description: 空间更新请求，提供给管理员使用，可以进行更新空间级别和限额
 *
 * @Create: 2025/6/5 14:43
 * @Author: jay
 * @Version: 1.0
 */
@Data
public class SpaceUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    /**
     * 空间图片的最大总大小
     */
    private Long maxSize;

    /**
     * 空间图片的最大数量
     */
    private Long maxCount;

    private static final long serialVersionUID = 1L;
}

