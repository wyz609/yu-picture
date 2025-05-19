package com.yupi.yupicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * Class name: PictureUploadRequest
 * Package: com.yupi.yupicturebackend.model.dto.picture
 * Description:
 *
 * @Create: 2025/5/18 14:14
 * @Author: jay
 * @Version: 1.0
 */

@Data
public class PictureUploadRequest implements Serializable {

    /*
     *  图片id(用于修改)
     */
    private Long id;

    private static final long serialVersionUID = 1L;

}
