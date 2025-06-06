package com.yupi.yupicturebackend.model.dto.space;

import com.yupi.yupicturebackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Class name: SpaceQueryRequest
 * Package: com.yupi.yupicturebackend.model.dto.space
 * Description: 空间查询请求
 *
 * @Create: 2025/6/5 14:48
 * @Author: jay
 * @Version: 1.0
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    private static final long serialVersionUID = 1L;
}
