package com.yupi.yupicturebackend.model.dto.picture;

import lombok.Data;

/**
 * Class name: PictureUploadByBatchRequest
 * Package: com.yupi.yupicturebackend.model.dto.picture
 * Description:
 *
 * @Create: 2025/5/29 22:51
 * @Author: jay
 * @Version: 1.0
 */
@Data
public class PictureUploadByBatchRequest {

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 名称前缀
     */
    private String namePrefix;

    /**
     * 抓取数量
     */
    private Integer count = 10;
}
