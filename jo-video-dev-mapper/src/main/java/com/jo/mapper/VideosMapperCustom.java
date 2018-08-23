package com.jo.mapper;

import com.jo.pojo.Videos;
import com.jo.pojo.vo.VideosVo;
import com.jo.utils.MyMapper;

import java.util.List;

public interface VideosMapperCustom extends MyMapper<Videos> {
    public List<VideosVo> queryAllVideos();
}