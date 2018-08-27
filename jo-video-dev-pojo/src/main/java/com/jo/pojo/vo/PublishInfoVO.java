package com.jo.pojo.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Id;

@ApiModel(value = "这是发布者对象", description="发布者对象")
public class PublishInfoVO {
    public UsersVO publishInfo;
    public boolean userLikeVideo;

    public UsersVO getPublishInfo() {
        return publishInfo;
    }

    public void setPublishInfo(UsersVO publishInfo) {
        this.publishInfo = publishInfo;
    }

    public boolean isUserLikeVideo() {
        return userLikeVideo;
    }

    public void setUserLikeVideo(boolean userLikeVideo) {
        this.userLikeVideo = userLikeVideo;
    }
}