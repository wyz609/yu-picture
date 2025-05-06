package com.yupi.yupicturebackend.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * Class name: UserRoleEnum
 * Package: com.yupi.yupicturebackend.model.enums
 * Description:
 *
 * @Create: 2025/5/6 17:29
 * @Author: jay
 * @Version: 1.0
 */
@Getter
public enum UserRoleEnum {

    USER("用户","user"),
    ADMIN("管理员","admin");

    private final String text;

    private final String value;

    UserRoleEnum(String text, String value){

        this.text = text;
        this.value = value;
    }

    /**
     * 根据value获取枚举对象
     * @param value 枚举值的value
     * @return 枚举值
     */
    public static UserRoleEnum getEnumByValue(String value){
        if(ObjUtil.isEmpty(value)){
            return null;
        }

        for(UserRoleEnum anEnum : UserRoleEnum.values()){
            if(anEnum.getValue().equals(value)){
                return anEnum;
            }
        }
        return null;
    }
}
