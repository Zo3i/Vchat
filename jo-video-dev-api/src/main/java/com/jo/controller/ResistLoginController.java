package com.jo.controller;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.jo.pojo.Users;
import com.jo.pojo.vo.UsersVO;
import com.jo.service.UserService;
import com.jo.utils.JSONResult;
import com.jo.utils.MD5Utils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;


@RestController
@Api(value="用户注册登录接口", tags= {"注册和登录的controller"})
public class ResistLoginController extends BasicController{
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
		UsersVO userVo = setRedisSession(users);
		return JSONResult.ok(userVo);
	}
	@PostMapping("/login")
	@ApiOperation(value="用户注册", notes="注册")
	public JSONResult login(@RequestBody Users user) throws Exception {
		//判断用户输入是否为空
		if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword()))
			return JSONResult.errorMsg("用户和密码不能为空");
		Users userResult = userserivce.queryUser(user.getUsername(), MD5Utils.getMD5Str(user.getPassword()));
		//如果查询到用户的账号密码,则返回用户对象
		if (userResult != null) {
			userResult.setPassword("");
			UsersVO userVo = setRedisSession(userResult);
			return JSONResult.ok(userVo);
		} else {
			return JSONResult.errorMsg("账号或密码错误");
		}
	}
	
	@ApiOperation(value="注销", notes="用户退出")
	@ApiImplicitParam(name = "userId", value = "用户ID", required = true,
					  dataType = "String", paramType = "query" )
	@PostMapping("/logout")
	public JSONResult logout(String userId) {
		redis.del(USER_REDIS_SESSION + ":" + userId);
		return JSONResult.ok();
	}
	
	public UsersVO setRedisSession(Users users) {
		String uniqueToken = UUID.randomUUID().toString();
		//设置session过期时间为30分钟
		redis.set(USER_REDIS_SESSION + ":" + users.getId(), uniqueToken, 1000 * 60 * 30);
		UsersVO userVo = new UsersVO();
		BeanUtils.copyProperties(users, userVo);
		userVo.setUserToken(uniqueToken);
		return userVo;
	}
}
