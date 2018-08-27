package com.jo.mapper;

import com.jo.pojo.Videos;
import com.jo.pojo.vo.VideosVo;
import com.jo.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VideosMapperCustom extends MyMapper<Videos> {
     List<VideosVo> queryAllVideos(@Param("videoDesc") String videoDesc,
                                         @Param("userId") String userId);
     void addVideoLikeCount(String videoId);
     void reduceVideoLikeCount(String videoId);
     List<VideosVo> queryLikeVideos(@Param("userId") String userId);

}