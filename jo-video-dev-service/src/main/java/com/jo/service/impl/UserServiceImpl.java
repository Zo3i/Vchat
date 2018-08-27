package com.jo.service.impl;

import com.jo.mapper.UsersFansMapper;
import com.jo.mapper.UsersLikeVideosMapper;
import com.jo.pojo.UsersFans;
import com.jo.pojo.UsersLikeVideos;
import com.jo.utils.JSONResult;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jo.mapper.UsersMapper;
import com.jo.pojo.Users;
import com.jo.service.UserService;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UsersMapper usermapper;
	@Autowired
	private UsersFansMapper usersFansMapper;
	@Autowired
	private UsersLikeVideosMapper usersLikeVideosMapper;
	@Autowired
	private Sid sid;
	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public boolean queryUsernameExist(String username) {
		// TODO Auto-generated method stub
		Users user = new Users();
		user.setUsername(username);
		Users result = usermapper.selectOne(user);
		return result == null ? false : true;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveUser(Users user) {
		// TODO Auto-generated method stub
		String userId = sid.nextShort();
		user.setId(userId);
		usermapper.insert(user);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public Users queryUser(String username, String password) {
		// TODO Auto-generated method stub
		Example userExample = new Example(Users.class);
		Criteria criteria = userExample.createCriteria();
		criteria.andEqualTo("username", username);
		criteria.andEqualTo("password", password);
		Users resulet = usermapper.selectOneByExample(userExample);
		return resulet;
		
	}

	@Override
	public void updateUserInfo(Users user) {
		// TODO Auto-generated method stub
		Example userExample = new Example(Users.class);
		Criteria criteria = userExample.createCriteria();
		criteria.andEqualTo("id", user.getId());
		usermapper.updateByExampleSelective(user, userExample);
	}

	@Override
	public Users queryUserInfo(String userId) {
		// TODO Auto-generated method stub
		Example userExample = new Example(Users.class);
		Criteria criteria = userExample.createCriteria();
		criteria.andEqualTo("id", userId);
		Users users = usermapper.selectOneByExample(userExample);
		return users;
	}

	@Override
	public boolean isUserLikeVideo(String userId, String videoId) {

		if(StringUtils.isBlank(userId) || StringUtils.isBlank(videoId)) {
			return false;
		}
		Example example = new Example(UsersLikeVideos.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("videoId", videoId);
		List<UsersLikeVideos> list = usersLikeVideosMapper.selectByExample(example);
		System.out.println(list.size());
		System.out.println(userId);
		System.out.println(videoId);
		if (list.size() > 0 && list != null) {
			return  true;
		} else {
			return false;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void saveUserFanRelation(String userId, String fanId) {
		String relId = sid.nextShort();
		UsersFans usersFans = new UsersFans();
		usersFans.setId(relId);
		usersFans.setFanId(fanId);
		usersFans.setUserId(userId);
		usersFansMapper.insert(usersFans);
		usermapper.addFansCount(userId);
		usermapper.addFollersCount(fanId);
	}

	@Override
	public void delUserFanRelation(String userId, String fanId) {
		Example example = new Example(UsersFans.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("fanId", fanId);

		usersFansMapper.deleteByExample(example);
		usermapper.reduceFansCount(userId);
		usermapper.reduceFollersCount(fanId);
	}

	@Override
	public boolean queryIsFans(String userId, String fanId) {
		Example example = new Example(UsersFans.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("fanId", fanId);

		List<UsersFans> list = usersFansMapper.selectByExample(example);

		if(list != null && !list.isEmpty() && list.size() > 0 ) {
			return true;
		}
		return false;
	}

}
