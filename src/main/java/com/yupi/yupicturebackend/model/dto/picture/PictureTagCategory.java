package com.yupi.yupicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Class name: PictureTagCategory
 * Package: com.yupi.yupicturebackend.model.entity
 * Description:
 *
 * @Create: 2025/5/19 22:20
 * @Author: jay
 * @Version: 1.0
 */
@Data
public class PictureTagCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 分类列表
     */
    private List<String> categoryList;

}
