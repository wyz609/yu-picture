package com.yupi.yupicturebackend.controller;

import com.yupi.yupicturebackend.annotation.AuthCheck;
import com.yupi.yupicturebackend.common.BaseResponse;
import com.yupi.yupicturebackend.common.ResultUtils;
import com.yupi.yupicturebackend.constant.UserConstant;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import com.yupi.yupicturebackend.manager.CosManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;

/**
 * Class name: FileController
 * Package: com.yupi.yupicturebackend.controller
 * Description:
 *
 * @Create: 2025/5/18 14:33
 * @Author: jay
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private CosManager cosManager;

    /**
     * 测试文件上传
     *
     * @param multipartFile 上传的文件
     * @return 文件的访问地址
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/test/upload")
    public BaseResponse<String> testUplodFile(@RequestPart("file") MultipartFile multipartFile){

        String filename = multipartFile.getOriginalFilename();
        String filepath = String.format("/test/%S", filename);
        File file = null;
        try {
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            // 返回可访问地址
            return ResultUtils.success(filepath);
        } catch (Exception e) {
            log.error("file upload error, filepaht = {}", filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"文件上传失败");
        } finally {
            if(file != null){
                boolean delete = file.delete();
                if(!delete){
                    log.error("file delete error,filepath={}", filepath);
                }
            }
        }

    }

}
