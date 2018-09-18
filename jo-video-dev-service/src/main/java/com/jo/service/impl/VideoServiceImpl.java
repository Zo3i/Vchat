package com.jo.service.impl;

import java.util.Date;
import java.util.List;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jo.mapper.*;
import com.jo.pojo.*;
import com.jo.pojo.vo.CommentsVo;
import com.jo.pojo.vo.VideosVo;
import com.jo.utils.JSONResult;
import com.jo.utils.PagedResult;
import com.jo.utils.TimeAgoUtils;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jo.service.BgmService;
import com.jo.service.UserService;
import com.jo.service.VideoService;

import org.springframework.web.bind.annotation.GetMapping;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class VideoServiceImpl implements VideoService {

	@Autowired
	private VideosMapper videoMapper;
	@Autowired
	private UsersMapper usersMapper;
	@Autowired
	private CommentsMapper commentsMapper;
	@Autowired
	private VideosMapperCustom videosMapperCustom;
	@Autowired
	private SearchRecordsMapper searchRecordsMapper;
	@Autowired
	private UsersLikeVideosMapper usersLikeVideosMapper;
	@Autowired
	private CommentsCustomMapper commentsCustomMapper;
	@Autowired
	private Sid sid;
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public String saveVideo(Videos video) {
		// TODO Auto-generated method stub
		String id = sid.nextShort(); 
		video.setId(id);
		videoMapper.insertSelective(video);
		return id;
		
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void updateVideo(String videoId, String coverPath) {
		// TODO Auto-generated method stub
		Videos video = new Videos();
		video.setId(videoId);
		video.setCoverPath(coverPath);
		videoMapper.updateByPrimaryKeySelective(video);
		
	}
	/**
	 * @Desciption: 获取视频列表
	 * @version:v-1.00
	 * @return: PagedResult
	 * @author:张琪灵
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public PagedResult getAllVideos(Videos video, Integer isSave,
									Integer page, Integer pageSize) {

		//保存热搜词
		String desc = video.getVideoDesc();
		String userId = video.getUserId();
		System.out.println("查询用户ID: " + userId);
		if (isSave != null && isSave == 1) {
			SearchRecords records = new SearchRecords();
			String recordId = sid.nextShort();
			records.setId(recordId);
			records.setContent(desc);
			searchRecordsMapper.insert(records);
		}

		PageHelper.startPage(page, pageSize);
		List<VideosVo> list = videosMapperCustom.queryAllVideos(desc, userId);
		for (VideosVo s : list) {
			Integer width = s.getVideoWidth();
			Integer height = s.getVideoHeight();
			if (width > height) {
				s.setMode("widthFix");
			} else {
				s.setMode("aspectFit");
			}
		}
		PageInfo<VideosVo> pageList = new PageInfo<>(list);

		PagedResult pagedResult = new PagedResult();
		pagedResult.setPage(page);
		pagedResult.setTotal(pageList.getPages());
		pagedResult.setRows(list);
		pagedResult.setRecords(pageList.getTotal());
		return pagedResult;
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public List<String> getHotWords() {
		return searchRecordsMapper.getHotWords();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void userLikeVideo(String userId, String videoId, String videoCreaterId) {
		//保存用户和视频点赞关系
		String likeId = sid.nextShort();
		UsersLikeVideos ulv = new UsersLikeVideos();
		ulv.setId(likeId);
		ulv.setUserId(userId);
		ulv.setVideoId(videoId);
		//视频喜欢数量累加
		videosMapperCustom.addVideoLikeCount(videoId);
		//用户视频被喜欢数量
		usersMapper.addReceiveLikeCount(videoCreaterId);
		usersLikeVideosMapper.insert(ulv);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void userDislikeVideo(String userId, String videoId, String videoCreaterId) {
		//删除用户和视频点赞关系
		Example example = new Example(UsersLikeVideos.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("videoId", videoId);
		usersLikeVideosMapper.deleteByExample(example);
		//视频喜欢数量累加
		videosMapperCustom.reduceVideoLikeCount(videoId);
		//用户视频被喜欢数量
		usersMapper.reduceReceiveLikeCount(videoCreaterId);
	}

	@Override
	public PagedResult queryLikeVideos(String userId, Integer page, Integer pageSize) {

		PageHelper.startPage(page, pageSize);
		List<VideosVo> list = videosMapperCustom.queryLikeVideos(userId);
		PageInfo<VideosVo> pageList = new PageInfo<>(list);

		PagedResult pagedResult = new PagedResult();
		pagedResult.setPage(page);
		pagedResult.setTotal(pageList.getPages());
		pagedResult.setRows(list);
		pagedResult.setRecords(pageList.getTotal());
		return pagedResult;

	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void saveComment(Comments comment) {
		String id = sid.nextShort();
		comment.setId(id);
		comment.setCreateTime(new Date());
		commentsMapper.insert(comment);
	}

	@Override
	public PagedResult getComments(String videoId, Integer page, Integer pageSize) {
		PageHelper.startPage(page, pageSize);
		List<CommentsVo> list = commentsCustomMapper.queryCommtes(videoId);
		for (CommentsVo c : list) {
			String timeAgo = TimeAgoUtils.format(c.getCreateTime());
			c.setTimeAgo(timeAgo);
		}
		PageInfo<CommentsVo> pageList = new PageInfo<>(list);

		PagedResult pagedResult = new PagedResult();
		pagedResult.setPage(page);
		pagedResult.setTotal(pageList.getPages());
		pagedResult.setRows(list);
		pagedResult.setRecords(pageList.getTotal());
		return pagedResult;
	}

}
