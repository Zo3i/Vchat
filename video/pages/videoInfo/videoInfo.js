var videoUtil = require('../../utils/videoUtil.js')

const app = getApp()

Page({
  data: {
    cover: "cover",
    videoId: "",
    src: "",
    videoInfo: {},
    userLikeVideo:false,
    publishInfo:{},
    serverUrl:""
  },

  showSearch: function () {
    wx.navigateTo({
      url: '../searchVideo/searchVideo',
    })
  },
  onLoad: function (params) {
    var me = this;
    console.log(params)
    var videoInfo = JSON.parse(params.videoInfo)
    var height = videoInfo.videoHeight
    var width = videoInfo.videoWidth
    console.log("height="+height)
    console.log("width="+width)
    var cover = 'cover'
    if (width > height) {
      console.log("宽视频")
      cover = ''
    }
    me.setData({
      videoId: videoInfo.id,
      videoInfo: videoInfo,
      src: app.serverUrl + videoInfo.videoPath,
      cover: cover
    })
    me.videoCtx = wx.createVideoContext("myVideo", this);

    //获取视频发布者信息,以及当前页面用户是否点赞
    var serverUrl = app.serverUrl;
    var user = app.getGloableUserInfo();
    var loginUserId = "";
    if(user != "" && user != undefined && user != null) {
      loginUserId = user.id;
    }
    wx.showLoading({
      title: '请等待...',
    })
    wx.request({
      url: serverUrl + '/user/queryPublishInfo?loginUserId=' + loginUserId + "&videoId=" 
                     + videoInfo.id + "&publishUserId=" + videoInfo.userId,
      method: "POST",
      header:{
        "content-type" : "application/json",
        "userId": user.id,
        "userToken": user.userToken
      },
      success: function (res) {
        wx.hideLoading();
        console.log("获取信息....")
        console.log(res.data.data);
        var result = res.data.data
        var userLikeVideo = res.data.data.userLikeVideo
        me.setData({
          publishInfo: result.publishInfo,
          serverUrl:app.serverUrl,
          userLikeVideo: result.userLikeVideo
        })
      }
    })


  },
  onShow: function () {
    var me = this;
    me.videoCtx.play();
  },
  onHide: function () {
    var me = this;
    me.videoCtx.pause();
  },
  upload: function () {
    var me = this
    var user = app.getGloableUserInfo()
    var videoInfo = JSON.stringify(me.data.videoInfo)
    var backUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfo
    if (user == null || user == undefined || user == '') {
      wx.navigateTo({
        url: '../userLogin/login?redictUrl=' + backUrl,
      })
    } else {
      videoUtil.uplodaVideo()
    }
  },
  showMine:function () {
    var user = app.getGloableUserInfo()
    if (user == null || user == undefined || user =='') {
      wx.navigateTo({
        url: '../userLogin/login',
      })
    } else {
      wx.navigateTo({
        url: '../mine/mine',
      })
    }
  },
  //用户点赞,取消点赞
  likeVideoOrNot: function () {
    var me = this;
    var videoInfo = me.data.videoInfo;
    console.log(videoInfo)
    var user = app.getGloableUserInfo()
    if (user == null || user == undefined || user == '') {
      wx.navigateTo({
        url: '../userLogin/login',
      })
    } else {
      var userLikeVideo = me.data.userLikeVideo;
      var url = '/video/userLike?userId=' + user.id + '&videoId=' 
                                          + videoInfo.id + '&videoCreatorId='
                                          + videoInfo.userId
      if (userLikeVideo) {
        url = '/video/userDislike?userId=' + user.id + '&videoId='
          + videoInfo.id + '&videoCreatorId='
          + videoInfo.userId
      }      

      var serverUrl = app.serverUrl;
      wx.showLoading({
        title: '请等待...',
      })
      wx.request({
        url: serverUrl + url,
        method: "POST",
        header: {
          'content-type': 'application/json',
          "userId": user.id,
          "userToken": user.userToken
        },
        success: function (res) {
          wx.hideLoading();
          me.setData({
            userLikeVideo: !userLikeVideo
          })
        }
      })                           
      
    }
  },
  //跳转作者信息
  showPublisher: function () {
    var me = this;
    var user = app.getGloableUserInfo();
    var videoInfo = me.data.videoInfo;
    var redictUrl = '../mine/mine#pulishId@' + videoInfo.userId;
    console.log(videoInfo.userId)
    if(user == null || user == undefined || user== "") {
      wx.navigateTo({
        url: '../userLogin/login?backUrl=' + redictUrl,
      })
    } else {
      wx.navigateTo({
        url: '../mine/mine?pulishId=' + videoInfo.userId,
      })
    }
  },
  //跳转主页;
  showIndex: function () {
    wx.navigateTo({
      url: '../index/index',
    })
  }

})