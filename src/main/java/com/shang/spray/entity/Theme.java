package com.shang.spray.entity;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;

/**
 * info:
 * Created by shang on 2017/5/11.
 */
@Entity
@DynamicUpdate
public class Theme extends BaseEntity{

    private String title; //名称

    private String code;  //标识

    private String description;  //描述

    private String sample;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }
}
