package com.jo.service;

import java.util.List;

import com.jo.pojo.Bgm;
import com.jo.pojo.Users;

public interface BgmService {
	/**
	 * 查询背景音乐
	 * @return
	 */
	List<Bgm> queryBgmList();
	/**
	 * @param bgmId
	 * 根据ID查询背景音乐
	 * @return
	 */
	Bgm queryById(String bgmId);
}
