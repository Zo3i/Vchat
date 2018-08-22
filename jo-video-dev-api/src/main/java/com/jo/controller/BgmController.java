package com.jo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jo.service.BgmService;
import com.jo.utils.JSONResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value="背景音乐", tags="背景音乐controller")
@RequestMapping("/bgm")
public class BgmController {
	
	@Autowired
	private BgmService bgmservice;
	@ApiOperation(value="查询所有背景音乐", notes="获取音乐接口")
	@PostMapping("/list")
	public JSONResult list() {
		return JSONResult.ok(bgmservice.queryBgmList());
	}
}
