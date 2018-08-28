package com.jo.mapper;

import com.jo.pojo.Users;
import com.jo.pojo.vo.UsersVO;
import com.jo.utils.MyMapper;

import java.util.List;

public interface UsersMapper extends MyMapper<Users> {
    /**
     * @Desciption: 增加获赞数
     * @version:v-1.00
     * @return:
     * @author:张琪灵
     */
    void addReceiveLikeCount(String userId);
    /**
     * @Desciption:减少获赞数
     * @version:v-1.00
     * @return:
     * @author:张琪灵
     */
    void reduceReceiveLikeCount(String userId);
    	/**
	 * @Desciption:增加粉丝数
	 * @version:v-1.00
	 * @return:
	 * @author:张琪灵
	 */
	void addFansCount(String userId);
	/**
	 * @Desciption:减少粉丝数量
	 * @version:v-1.00
	 * @return:
	 * @author:张琪灵
	 */
	void reduceFansCount(String userId);
	/**
	 * @Desciption:增加跟随者数量
	 * @version:v-1.00
	 * @return:
	 * @author:张琪灵
	 */
	void addFollersCount(String userId);
	/**
	 * @Desciption:减少跟随者数量
	 * @version:v-1.00
	 * @return:
	 * @author:张琪灵
	 */
	void reduceFollersCount(String userId);
	List<Users> queryFollow(String userId);
}