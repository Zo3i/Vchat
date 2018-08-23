package com.jo.service;

import java.util.List;

import com.jo.pojo.Bgm;
import com.jo.pojo.Users;
import com.jo.pojo.Videos;
import com.jo.utils.PagedResult;

public interface VideoService {
	/**
	 * 保存视频信息
	 * @return
	 */
	String saveVideo(Videos video);
	
	/**
	 * @param videoId
	 * @param userId
	 * 更新视频封面
	 */
	void updateVideo(String videoId,String coverPath);

	/**
	 * @Desciption:获取视频列表
	 * @version:v-1.00
	 * @return: getAllVideos
	 * @author:张琪灵
	 */
	PagedResult getAllVideos(Integer page, Integer pageSize);
}
