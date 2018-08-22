package com.jo.service.impl;

import org.apache.catalina.User;
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

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UsersMapper usermapper;
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

}
