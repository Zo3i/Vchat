package com.jo.mapper;

import com.jo.pojo.vo.CommentsVo;
import com.jo.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentsCustomMapper extends MyMapper<CommentsVo> {
    List<CommentsVo> queryCommtes(@Param("videoId") String videoId);
}