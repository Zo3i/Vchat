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
import com.jo.pojo.Bgm;
import com.jo.pojo.Users;
import com.jo.service.BgmService;
import com.jo.service.UserService;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class BgmServiceImpl implements BgmService {

	@Autowired
	private BgmMapper bgmMapper;
	@Autowired
	private Sid sid;
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public List<Bgm> queryBgmList() {
		// TODO Auto-generated method stub
		return bgmMapper.selectAll();
	}
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public Bgm queryById(String bgmId) {
		// TODO Auto-generated method stub
		return bgmMapper.selectByPrimaryKey(bgmId);
	}


}
