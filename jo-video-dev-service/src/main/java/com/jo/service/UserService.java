package com.jo.service;

import com.jo.pojo.Users;
import com.jo.pojo.UsersReport;
import com.jo.utils.PagedResult;

import java.util.List;

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
	/**
	 * @param userId
	 * @Desciption:查询用户信息
	 * @return
	 */
	Users queryUserInfo(String userId);

	/**
	 * @Desciption: 用户是否点赞
	 * @version:v-1.00
	 * @return:
	 * @author:张琪灵
	 */
	boolean isUserLikeVideo(String userId, String videoId);
	/**
	 * @Desciption:添加用户与粉丝的关系
	 * @version:v-1.00
	 * @return:
	 * @author:张琪灵
	 */
	void saveUserFanRelation(String userId, String fanId);
	/**
	 * @Desciption:删除用户与粉丝的关系
	 * @version:v-1.00
	 * @return:
	 * @author:张琪灵
	 */
	void delUserFanRelation(String userId, String fanId);
	/**
	 * @Desciption: 查询是不是粉丝
	 * @version:v-1.00
	 * @return:
	 * @author:张琪灵
	 */
	boolean queryIsFans(String userId, String fanId);
	/**
	 * @Desciption:查询用户关注;
	 * @version:v-1.00
	 * @return:
	 * @author:张琪灵
	 */
	PagedResult queryFollow(String userId, Integer page, Integer pageSize);
	/**
	 * @Desciption:举报用户
	 * @version:v-1.00
	 * @return:
	 * @author:张琪灵
	 */
	void reportUser(UsersReport usersReport);
}
