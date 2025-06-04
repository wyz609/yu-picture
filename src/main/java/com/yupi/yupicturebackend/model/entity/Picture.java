package com.yupi.yupicturebackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 图片
 * @TableName picture
 */

@TableName(value ="picture")
@Data
public class Picture implements Serializable {


    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * id
     * -- GETTER --
     *  id
     * -- SETTER --
     *  id


     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 图片 url
     * -- GETTER --
     *  图片 url
     * -- SETTER --
     *  图片 url


     */
    private String url;

    /**
     * 图片名称
     * -- GETTER --
     *  图片名称
     * -- SETTER --
     *  图片名称


     */
    private String name;

    /**
     * 简介
     * -- GETTER --
     *  简介
     * -- SETTER --
     *  简介


     */
    private String introduction;

    /**
     * 分类
     * -- GETTER --
     *  分类
     * -- SETTER --
     *  分类


     */
    private String category;

    /**
     * 标签（JSON 数组）
     * -- GETTER --
     *  标签（JSON 数组）
     * -- SETTER --
     *  标签（JSON 数组）


     */
    private String tags;

    /**
     * 图片体积
     * -- GETTER --
     *  图片体积
     * -- SETTER --
     *  图片体积


     */
    private Long picSize;

    /**
     * 图片宽度
     * -- GETTER --
     *  图片宽度
     * -- SETTER --
     *  图片宽度


     */
    private Integer picWidth;

    /**
     * 图片高度
     * -- GETTER --
     *  图片高度
     * -- SETTER --
     *  图片高度


     */
    private Integer picHeight;

    /**
     * 图片宽高比例
     * -- GETTER --
     *  图片宽高比例
     * -- SETTER --
     *  图片宽高比例


     */
    private Double picScale;

    /**
     * 图片格式
     * -- GETTER --
     *  图片格式
     * -- SETTER --
     *  图片格式


     */
    private String picFormat;

    /**
     * 创建用户 id
     * -- GETTER --
     *  创建用户 id
     * -- SETTER --
     *  创建用户 id


     */
    private Long userId;

    /**
     * 创建时间
     * -- GETTER --
     *  创建时间
     * -- SETTER --
     *  创建时间


     */
    private Date createTime;

    /**
     * 编辑时间
     * -- GETTER --
     *  编辑时间
     * -- SETTER --
     *  编辑时间


     */
    private Date editTime;

    /**
     * 更新时间
     * -- GETTER --
     *  更新时间
     * -- SETTER --
     *  更新时间


     */
    private Date updateTime;

    /**
     * 是否删除
     * -- GETTER --
     *  是否删除
     * -- SETTER --
     *  是否删除


     */
    @TableLogic
    private Integer isDelete;

    /**
     * 审核状态 0- 待审核, 1 - 审核通过, 2 - 审核不通过
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;


    /**
     * 审核id
     */
    private Long reviewerId;

    /**
     * 缩略图URL
     */
    private String thumbnailUrl;

    /**
     * 审核时间
     */
    private Date reviewTime;



    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Picture other = (Picture) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUrl() == null ? other.getUrl() == null : this.getUrl().equals(other.getUrl()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getIntroduction() == null ? other.getIntroduction() == null : this.getIntroduction().equals(other.getIntroduction()))
            && (this.getCategory() == null ? other.getCategory() == null : this.getCategory().equals(other.getCategory()))
            && (this.getTags() == null ? other.getTags() == null : this.getTags().equals(other.getTags()))
            && (this.getPicSize() == null ? other.getPicSize() == null : this.getPicSize().equals(other.getPicSize()))
            && (this.getPicWidth() == null ? other.getPicWidth() == null : this.getPicWidth().equals(other.getPicWidth()))
            && (this.getPicHeight() == null ? other.getPicHeight() == null : this.getPicHeight().equals(other.getPicHeight()))
            && (this.getPicScale() == null ? other.getPicScale() == null : this.getPicScale().equals(other.getPicScale()))
            && (this.getPicFormat() == null ? other.getPicFormat() == null : this.getPicFormat().equals(other.getPicFormat()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getEditTime() == null ? other.getEditTime() == null : this.getEditTime().equals(other.getEditTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUrl() == null) ? 0 : getUrl().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getIntroduction() == null) ? 0 : getIntroduction().hashCode());
        result = prime * result + ((getCategory() == null) ? 0 : getCategory().hashCode());
        result = prime * result + ((getTags() == null) ? 0 : getTags().hashCode());
        result = prime * result + ((getPicSize() == null) ? 0 : getPicSize().hashCode());
        result = prime * result + ((getPicWidth() == null) ? 0 : getPicWidth().hashCode());
        result = prime * result + ((getPicHeight() == null) ? 0 : getPicHeight().hashCode());
        result = prime * result + ((getPicScale() == null) ? 0 : getPicScale().hashCode());
        result = prime * result + ((getPicFormat() == null) ? 0 : getPicFormat().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getEditTime() == null) ? 0 : getEditTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", url=").append(url);
        sb.append(", name=").append(name);
        sb.append(", introduction=").append(introduction);
        sb.append(", category=").append(category);
        sb.append(", tags=").append(tags);
        sb.append(", picSize=").append(picSize);
        sb.append(", picWidth=").append(picWidth);
        sb.append(", picHeight=").append(picHeight);
        sb.append(", picScale=").append(picScale);
        sb.append(", picFormat=").append(picFormat);
        sb.append(", userId=").append(userId);
        sb.append(", createTime=").append(createTime);
        sb.append(", editTime=").append(editTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append("]");
        return sb.toString();
    }
}