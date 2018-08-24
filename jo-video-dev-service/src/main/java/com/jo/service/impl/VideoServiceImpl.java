package com.jo.service.impl;

import java.util.List;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jo.mapper.*;
import com.jo.pojo.SearchRecords;
import com.jo.pojo.vo.VideosVo;
import com.jo.utils.PagedResult;
import org.apache.catalina.User;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jo.pojo.Bgm;
import com.jo.pojo.Users;
import com.jo.pojo.Videos;
import com.jo.service.BgmService;
import com.jo.service.UserService;
import com.jo.service.VideoService;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class VideoServiceImpl implements VideoService {

	@Autowired
	private VideosMapper videoMapper;
	@Autowired
	private VideosMapperCustom videosMapperCustom;
	@Autowired
	private SearchRecordsMapper searchRecordsMapper;
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
		if (isSave != null && isSave == 1) {
			SearchRecords records = new SearchRecords();
			String recordId = sid.nextShort();
			records.setId(recordId);
			records.setContent(desc);
			searchRecordsMapper.insert(records);
		}

		PageHelper.startPage(page, pageSize);
		List<VideosVo> list = videosMapperCustom.queryAllVideos(desc);
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

}
