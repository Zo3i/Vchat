package com.jo.service;

import com.jo.pojo.Users;

public interface UserService {
 /**
  * 判断用户名是否存在
  * */
	boolean queryUsernameExist(String username);
	/**
	 * @注册用户
	 */
	void saveUser(Users user);

	/**
	 * @param username
	 * @param password  
	 * @return 验证用户登录
	 */
	Users queryUser(String username, String password);
	
	
	/**
	 * @param user
	 * @更新用户信息
	 */
	void updateUserInfo(Users user);

}
