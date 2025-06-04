package com.yupi.yupicturebackend.model.vo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.yupi.yupicturebackend.model.entity.Picture;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Class name: PictureVO
 * Package: com.yupi.yupicturebackend.model.vo
 * Description:
 *
 * @Create: 2025/5/18 14:15
 * @Author: jay
 * @Version: 1.0
 */

@Data
public class PictureVO implements Serializable {

    private Long id;

    private String url;

    /**
     * 缩略图 url
     */
    private String thumbnailUrl;

    private String name;

    private String introduction;

    private List<String> tags;

    private String category;

    private Long picSize;

    private Integer picHeight;

    private Double picScale;

    private Long userId;

    private Date createTime;

    private Date updateTime;

    private UserVO user;

    private Integer picWidth;

    private String picFormat;

    private Date editTime;

    private static final long serialVersionUID = 1L;

    /**
     * 类型转换对象
     * @param pictureVO 脱敏后的图片对象
     * @return 图片对象
     */
    public static Picture voToObject(PictureVO pictureVO){
        if(pictureVO == null){
            return null;
        }

        Picture picture = new Picture();
        BeanUtil.copyProperties(pictureVO, picture);
        // 类型不同， 不需要转换
        picture.setTags(JSONUtil.toJsonStr(pictureVO.getTags()));
        return picture;
    }

    /**
     * 对象转封装类
     * @param picture 图片对象
     * @return 封装类
     */
    public static PictureVO objToVo(Picture picture){
        if(picture == null){
            return null;
        }

        PictureVO pictureVO = new PictureVO();
        BeanUtil.copyProperties(picture,pictureVO);
        // 类型不同，需要转换
        pictureVO.setTags(JSONUtil.toList(picture.getTags(),String.class));
        System.out.println("pictureVO =====> " + pictureVO);
        return pictureVO;
    }

}
