package com.jo.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.jo.utils.FetchVideoCover;
import com.jo.utils.PagedResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.jo.enums.VideoStatusEnum;
import com.jo.pojo.Bgm;
import com.jo.pojo.Users;
import com.jo.pojo.Videos;
import com.jo.service.BgmService;
import com.jo.service.UserService;
import com.jo.service.VideoService;
import com.jo.utils.JSONResult;
import com.jo.utils.MergeVideoMp3;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(value="视频相关业务", tags="视频业务controller")
@RequestMapping("/video")
public class VideoController extends BasicController{
	@Autowired
	private UserService userService;
	@Autowired 
	private BgmService bgmService;
	@Autowired
	private VideoService videoService;
	
	@ApiOperation(value = "上传视频", notes = "上传视频接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "userId", value = "用户Id", required = true, 
				  dataType = "String", paramType = "form"),
		@ApiImplicitParam(name = "bgmId", value = "背景音乐Id", required = false, 
		  dataType = "String", paramType = "form"),
		@ApiImplicitParam(name = "videoSeconds", value = "视频长度", required = true, 
		  dataType = "double", paramType = "form"),
		@ApiImplicitParam(name = "videoWidth", value = "视频宽度", required = true, 
		  dataType = "int", paramType = "form"),
		@ApiImplicitParam(name = "videoHeight", value = "视频高度", required = true, 
		  dataType = "int", paramType = "form"),
		@ApiImplicitParam(name = "desc", value = "视频描述", required = false, 
		  dataType = "String", paramType = "form")
	})

	@PostMapping(value="/uploadVideo", headers="content-type=multipart/form-data" )
	public JSONResult uplaodFace(String userId, String bgmId, 
			double videoSeconds, int videoWidth,
			int videoHeight, String desc,
			@ApiParam(value="短视频", required=true)
			MultipartFile file) throws Exception {
		
		if (StringUtils.isBlank(userId)) {
			return JSONResult.errorMsg("用户ID不能为空");
		}
		//数据库保存路径
		String uploadPathDB = "/" + userId + "/video";
		String coverPathDB = "/" + userId + "/video";
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		//文件的最终保存路径
		String finalVideoPath = "";
		try {
			if (file != null) {
				System.out.println("图片名字"+file.getOriginalFilename());
				String fileName = file.getOriginalFilename();
				String fileNamePre = fileName.split("\\.")[2];
				if (StringUtils.isNotBlank(fileName)) {
					finalVideoPath = FILE_SAVE_lOCATION + uploadPathDB + "/" + fileName;
					//设置数据库保存路径
					uploadPathDB += ("/" + fileName);
					coverPathDB += "/" + fileNamePre + ".jpg";
					File outFile = new File(finalVideoPath);
					if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
						//创建父文件夹
						outFile.getParentFile().mkdirs();
					}
					fileOutputStream = new FileOutputStream(outFile);
					inputStream = file.getInputStream();
					IOUtils.copy(inputStream, fileOutputStream);
				}
			} else {
				return JSONResult.errorMsg("上传出错!");
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			} else {
				return JSONResult.errorMsg("上传出错!");
			}
		}
		if (StringUtils.isNotBlank(bgmId)) {
			Bgm bgm = bgmService.queryById(bgmId);
			String mp3InputPath = FILE_SAVE_lOCATION + bgm.getPath();
			System.out.println("mp3=" + mp3InputPath);
			MergeVideoMp3 tool = new MergeVideoMp3(FFMPEG_EXE);
			String videoInputPath = finalVideoPath;
			String videoOutputName = UUID.randomUUID().toString() + ".mp4";
			uploadPathDB = "/" + userId + "/video/" + videoOutputName;
			finalVideoPath = FILE_SAVE_lOCATION + uploadPathDB;
			tool.convertor(videoInputPath, mp3InputPath, videoSeconds, finalVideoPath);
			
		}

		//视频截图
		FetchVideoCover videoInfo = new FetchVideoCover(FFMPEG_EXE);
		videoInfo.getCover(finalVideoPath, FILE_SAVE_lOCATION + coverPathDB);
		
		//保存视频信息到数据库
		Videos video = new Videos();
		video.setAudioId(bgmId);
		video.setUserId(userId);
		video.setVideoSeconds((float)videoSeconds);
		video.setVideoDesc(desc);
		video.setVideoHeight(videoHeight);
		video.setVideoWidth(videoWidth);
		video.setVideoPath(uploadPathDB);
		video.setCoverPath(coverPathDB);
		video.setStatus(VideoStatusEnum.SUCCESS.value);
		video.setCreateTime(new Date());
		String videoId = videoService.saveVideo(video);
		return JSONResult.ok(videoId);
	} 
	
	@ApiOperation(value = "上传视频封面", notes = "上传视频封面接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name="videoId", value="视频ID", required=true,
				dataType="string", paramType="form"),
		@ApiImplicitParam(name="userId", value="用户ID",required=true,
				dataType="string", paramType="form")
	})
	@PostMapping(value="/uploadVideoCover", headers="content-type=multipart/form-data" )
	public JSONResult uplaodViodeFace(String videoId,String userId,
			@ApiParam(value="视频封面", required=true)
			MultipartFile file) throws Exception {
		
		
		System.out.println("上传封面");
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(videoId) ) {
			return JSONResult.errorMsg("用户ID和视频ID不能为空");
		}
		//数据库保存路径
		String uploadPathDB = "/" + userId + "/video";
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		//文件的最终保存路径
		String finalVideoCoverPath = "";
		try {
			if (file != null) {
				String fileName = file.getOriginalFilename();
				if (StringUtils.isNotBlank(fileName)) {
					finalVideoCoverPath = FILE_SAVE_lOCATION + uploadPathDB + "/" + fileName;
					//设置数据库保存路径
					uploadPathDB += ("/" + fileName);
					File outFile = new File(finalVideoCoverPath);
					if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
						//创建父文件夹
						outFile.getParentFile().mkdirs();
					}
					fileOutputStream = new FileOutputStream(outFile);
					inputStream = file.getInputStream();
					IOUtils.copy(inputStream, fileOutputStream);
				}
			} else {
				return JSONResult.errorMsg("上传出错!");
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			} else {
				return JSONResult.errorMsg("上传出错!");
			}
		}
		System.out.println(uploadPathDB);
		videoService.updateVideo(videoId, uploadPathDB);
		return JSONResult.ok();
	}

	/**
	 * @Desciption:分页查询和搜索查询
	 * isSave: 1,需要保存 0不需要保存
	 * @version:v-1.00
	 * @return:
	 * @author:张琪灵
	 */
	@ApiOperation(value = "分页查询,搜索查询", notes = "视频查询接口")
	@PostMapping(value = "/showAll")
	public JSONResult showAll(@RequestBody Videos video, Integer isSave,
							  Integer page) {
		if (page == null) {
			page = 1;
		}
		PagedResult result = videoService.getAllVideos(video, isSave, page, PAGE_SIZE);
		return JSONResult.ok(result);
	}

	@PostMapping(value = "/hot")
	public JSONResult hot() {
		List<String> list = videoService.getHotWords();
		return JSONResult.ok(list);
	}
}
