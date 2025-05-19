package com.yupi.yupicturebackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

/**
 * 图片
 * @TableName picture
 */
@TableName(value ="picture")
public class Picture implements Serializable {


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private Long id;

    /**
     * 图片 url
     */
    private String url;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签（JSON 数组）
     */
    private String tags;

    /**
     * 图片体积
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private Integer picWidth;

    /**
     * 图片高度
     */
    private Integer picHeight;

    /**
     * 图片宽高比例
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * id
     */
    public Long getId() {
        return id;
    }

    /**
     * id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 图片 url
     */
    public String getUrl() {
        return url;
    }

    /**
     * 图片 url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 图片名称
     */
    public String getName() {
        return name;
    }

    /**
     * 图片名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 简介
     */
    public String getIntroduction() {
        return introduction;
    }

    /**
     * 简介
     */
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    /**
     * 分类
     */
    public String getCategory() {
        return category;
    }

    /**
     * 分类
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * 标签（JSON 数组）
     */
    public String getTags() {
        return tags;
    }

    /**
     * 标签（JSON 数组）
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * 图片体积
     */
    public Long getPicSize() {
        return picSize;
    }

    /**
     * 图片体积
     */
    public void setPicSize(Long picSize) {
        this.picSize = picSize;
    }

    /**
     * 图片宽度
     */
    public Integer getPicWidth() {
        return picWidth;
    }

    /**
     * 图片宽度
     */
    public void setPicWidth(Integer picWidth) {
        this.picWidth = picWidth;
    }

    /**
     * 图片高度
     */
    public Integer getPicHeight() {
        return picHeight;
    }

    /**
     * 图片高度
     */
    public void setPicHeight(Integer picHeight) {
        this.picHeight = picHeight;
    }

    /**
     * 图片宽高比例
     */
    public Double getPicScale() {
        return picScale;
    }

    /**
     * 图片宽高比例
     */
    public void setPicScale(Double picScale) {
        this.picScale = picScale;
    }

    /**
     * 图片格式
     */
    public String getPicFormat() {
        return picFormat;
    }

    /**
     * 图片格式
     */
    public void setPicFormat(String picFormat) {
        this.picFormat = picFormat;
    }

    /**
     * 创建用户 id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 创建用户 id
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 编辑时间
     */
    public Date getEditTime() {
        return editTime;
    }

    /**
     * 编辑时间
     */
    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    /**
     * 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 是否删除
     */
    public Integer getIsDelete() {
        return isDelete;
    }

    /**
     * 是否删除
     */
    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

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