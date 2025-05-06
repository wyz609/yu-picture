package com.yupi.yupicturebackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * Class name: DeleteReqeust
 * Package: com.yupi.yupicturebackend.common
 * Description:
 *
 * @Create: 2025/4/29 0:12
 * @Author: jay
 * @Version: 1.0
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;

}

