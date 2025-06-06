package com.yupi.yupicturebackend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Class name: SpaceLevel
 * Package: com.yupi.yupicturebackend.model.entity
 * Description: 该封装类定义了空间级别的信息
 *
 * @Create: 2025/6/6 10:03
 * @Author: jay
 * @Version: 1.0
 */

@Data
@AllArgsConstructor
public class SpaceLevel {

    private int value;

    private String text;

    private long maxCount;

    private long maxSize;

}
