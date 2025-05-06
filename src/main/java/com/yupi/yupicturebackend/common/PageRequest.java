package com.yupi.yupicturebackend.common;

import lombok.Data;

/**
 * Class name: PageRequest
 * Package: com.yupi.yupicturebackend.common
 * Description:
 *
 * @Create: 2025/4/29 0:10
 * @Author: jay
 * @Version: 1.0
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int current = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序(默认降序)
     */
    private String sortOrder = "descend";

}

