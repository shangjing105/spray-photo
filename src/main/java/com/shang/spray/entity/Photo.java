package com.shang.spray.entity;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;

/**
 * info:
 * Created by shang on 2017/4/27.
 */
@Entity
@DynamicUpdate
public class Photo extends BaseEntity {

    private String title; //名称

    private String link;  //链接

    private String description;  //描述

    private String author;

    private Integer userId;

    private String label;

    private Boolean placedTop;  //是否置顶

    private Boolean recommend;  //是否推荐

    private Integer status;  //状态

    private Integer albumId;  //专辑id(暂未使用)

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getPlacedTop() {
        return placedTop;
    }

    public void setPlacedTop(Boolean placedTop) {
        this.placedTop = placedTop;
    }

    public Boolean getRecommend() {
        return recommend;
    }

    public void setRecommend(Boolean recommend) {
        this.recommend = recommend;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
