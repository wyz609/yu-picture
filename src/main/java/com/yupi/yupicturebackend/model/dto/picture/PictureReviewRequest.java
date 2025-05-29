package com.yupi.yupicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * Class name: PictureReviewRequest
 * Package: com.yupi.yupicturebackend.model.dto.picture
 * Description:
 *
 * @Create: 2025/5/28 17:04
 * @Author: jay
 * @Version: 1.0
 */

@Data
public class PictureReviewRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 状态：0-待审核, 1-通过, 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;


    private static final long serialVersionUID = 1L;
}

