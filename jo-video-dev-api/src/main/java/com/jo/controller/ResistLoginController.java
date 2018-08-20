package com.jo.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jo.pojo.Users;
import com.jo.service.UserService;
import com.jo.utils.JSONResult;
import com.jo.utils.MD5Utils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;


@RestController
@Api(value="用户注册登录接口", tags= {"注册和登录的controller"})
public class ResistLoginController {
	@Autowired UserService userserivce;
	@ApiOperation(value="用户登录", notes="用户注册接口")
	@PostMapping("/regist")
	public JSONResult regist(@RequestBody Users users) throws Exception {
		//1.判断对象是否为空
		if (StringUtils.isBlank(users.getUsername()) || StringUtils.isBlank(users.getPassword())) {
			return JSONResult.errorMsg("用户名和密码不能为空"); 
		}
		//2.数据库中是否存在
		boolean userNameExist = userserivce.queryUsernameExist(users.getUsername());
		//3.保存用户
		if (!userNameExist) {
			users.setNickname(users.getUsername());
			users.setPassword(MD5Utils.getMD5Str(users.getPassword()));
			users.setFaceImage(null);
			users.setFansCounts(0);
			users.setFollowCounts(0);
			users.setReceiveLikeCounts(0);
			userserivce.saveUser(users);
		} else {
			return JSONResult.errorMsg("用户已存在,请换一个试试");
		}
		//删除密码信息
		users.setPassword("");
		return JSONResult.ok(users);
	}
	@PostMapping("/login")
	@ApiOperation(value="用户注册", notes="注册")
	public JSONResult login(@RequestBody Users user) throws Exception {
		if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword()))
			return JSONResult.errorMsg("用户和密码不能为空");
		Users userResult = userserivce.queryUser(user.getUsername(), MD5Utils.getMD5Str(user.getPassword()));
		if (userResult != null) {
			return JSONResult.ok(userResult);
		} else {
			return JSONResult.errorMsg("账号或密码错误");
		}
	}
}
