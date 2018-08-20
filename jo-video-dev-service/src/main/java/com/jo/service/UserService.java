package com.jo.service;

import com.jo.pojo.Users;

public interface UserService {
 /**
  * 判断用户名是否存在
  * */
	boolean queryUsernameExist(String username);
	/**
	 * 注册用户
	 */
	void saveUser(Users user);
	/***
	 *验证账号密码 
	 */
	Users queryUser(String username, String password);

}
