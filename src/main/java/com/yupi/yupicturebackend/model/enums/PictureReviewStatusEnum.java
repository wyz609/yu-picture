package com.yupi.yupicturebackend.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum PictureReviewStatusEnum {
    REVIEWING("审核中", 0),
    PASS("审核通过", 1),
    REJECT("审核拒绝", 2);

    private final String text;

    private final int value;

    PictureReviewStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value获取枚举
     * @param value
     * @return
     */
    public static PictureReviewStatusEnum getEnumByValue(Integer value){
        // 非空校验
        if(ObjUtil.isEmpty(value)){
            return null;
        }
        // 遍历枚举值
        for(PictureReviewStatusEnum status : PictureReviewStatusEnum.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }

}
