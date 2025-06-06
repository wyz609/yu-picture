package com.yupi.yupicturebackend.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * Class name: SpaceEditRequest
 * Package: com.yupi.yupicturebackend.model.dto.space
 * Description: 提供给用户使用的编辑空间请求，目前只能编辑空间名称
 *
 * @Create: 2025/6/5 14:42
 * @Author: jay
 * @Version: 1.0
 */
@Data
public class SpaceEditRequest implements Serializable {

    /**
     * 空间 id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    private static final long serialVersionUID = 1L;
}

