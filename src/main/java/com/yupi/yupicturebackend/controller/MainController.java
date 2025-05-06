package com.yupi.yupicturebackend.controller;

import com.yupi.yupicturebackend.common.BaseResponse;
import com.yupi.yupicturebackend.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class name: MainController
 * Package: com.yupi.yupicturebackend.controller
 * Description:
 *
 * @Create: 2025/4/29 0:21
 * @Author: jay
 * @Version: 1.0
 */
@RestController
@RequestMapping("/")
public class MainController {
    /**
     * 健康检查
     * @return 响应
     */
    @GetMapping("/health")
    public BaseResponse<String> health(){
        return ResultUtils.success("ok");
    }

}

