package com.jo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.jo.utils.RedisOperator;

@RestController
public class BasicController {
	@Autowired 
	public RedisOperator redis;
	
	//redis的key
	public static final String USER_REDIS_SESSION ="user_redis_session";
	//文件上传路径
	public static final String FILE_SAVE_lOCATION = "E:/WeixinApp/userFile"; 
	//ffmpeg
	public static final String FFMPEG_EXE = "E:/WeixinApp/userFile/ffmpeg/bin/ffmpeg.exe";
}
