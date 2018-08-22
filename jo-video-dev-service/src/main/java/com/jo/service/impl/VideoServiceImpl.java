package com.jo.service.impl;

import java.util.List;

import org.apache.catalina.User;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jo.mapper.BgmMapper;
import com.jo.mapper.UsersMapper;
import com.jo.mapper.VideosMapper;
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

}
