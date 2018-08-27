package com.jo.service;

import java.util.List;

import com.jo.pojo.Bgm;
import com.jo.pojo.Users;
import com.jo.pojo.Videos;
import com.jo.pojo.vo.VideosVo;
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
	PagedResult getAllVideos(Videos video, Integer isSave, Integer page, Integer pageSize);

	/**
	 * @Desciption: 获取热搜词
	 * @version:v-1.00
	 * @return:
	 * @author:张琪灵
	 */
	List<String> getHotWords();
	/**
	 * @Desciption:用户点赞
	 * @version:v-1.00
	 * @return:
	 * @author:张琪灵
	 */
	void userLikeVideo(String userId, String VideoId, String videoCreaterId);
	/**
	 * @Desciption:用户取消点赞
	 * @version:v-1.00
	 * @return:
	 * @author:张琪灵
	 */
	void userDislikeVideo(String userId, String VideoId, String videoCreaterId);
	PagedResult queryLikeVideos(String userId, Integer page, Integer pageSize);

}
