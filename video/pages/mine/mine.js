var videoUtil = require('../../utils/videoUtil.js')

const app = getApp()

Page({
  data: {
    faceUrl: "../resource/images/noneface.png",
    isMe: true,
    isFollow: false, 
    publishId:"",
    videoSelClass: "video-info",
    isSelectedWork: "video-info-selected",
    isSelectedLike: "",
    isSelectedFollow: "",

    myVideoList:[],
    myVideoPage:1,
    myVideoTotle:1,

    likeVideoList: [],
    likeVideoPage: 1,
    likeVideoTotle: 1,

    FollowList: [],
    FollowPage: 1,
    FollowTotle: 1,

    myWorkFlag: false,
    myLikesFlag: true,
    myFollowFlag:true

  },
  //页面加载获取用户数据
  onLoad: function (params) {
    var me = this;
    var user = app.getGloableUserInfo();
    var userId = user.id;
    console.log("用户ID" + params.pulishId)
    var publishId = params.pulishId;

    //获取默认的列表
    me.getVideoList(1, params.pulishId)

    if (publishId != "" && publishId != null && publishId != undefined) {
      if (publishId != userId) {
        me.setData({
          isMe: false,
          publishId: publishId
        })
      }
      userId = publishId;
    }

    wx.showLoading({
      title: '请等待..',
    })
    var serverUrl = app.serverUrl
    wx.request({
      url: serverUrl + '/user/query?userId=' + userId + "&fanId=" + user.id,
      method: "POST",
      header:{
        'content-type' : 'application/json',
        "userId": user.id,
        "userToken": user.userToken
      },
      success: function (res) {
        wx.hideLoading();
        if(res.data.status == 200) {
          var userInfo = res.data.data;
          var faceUrl = "../resource/images/noneface.png"
          if (userInfo.faceImage != null && userInfo.faceImage != "" &&                              userInfo.faceImage != undefined) {
             faceUrl = serverUrl + userInfo.faceImage
          }
          me.setData({
            //用户头像
            faceUrl: faceUrl,
            //用户粉丝数
            fansCounts: userInfo.fansCounts,
            //用户关注数
            followCounts: userInfo.followCounts,
            //用户获赞数
            receiveLikeCounts: userInfo.receiveLikeCounts,
            //用户昵称
            nickname: userInfo.nickname,
            //是否是粉丝
            isFollow:userInfo.follow
          })
        } else if (res.data.status == 500){
          console.log(res)
          wx.showToast({
            title: "网络错误咯...",
            icon: 'none',
            duration: 2000
          })
        }
      }
    });
  },
  //退出登录
  logout: function () {
    var user = app.getGloableUserInfo();
    var serverUrl = app.serverUrl;
    wx.showLoading({
      title: '请等待...',
    });
    wx.request({
      url: serverUrl + '/logout?userId=' + user.id,
      method: "POST",
      header: {
        'content-type' : 'application/json', //默认值
      },
      success: function (res) {
        wx.hideLoading();
        if (res.data.status == 200) {
          console.log(res.data)
          wx.showToast({
            title: '注销成功',
          })
          // app.userInfo = null
          wx.removeStorageSync("userInfo")
          wx.navigateTo({
            url: '../userLogin/login',
          })
        } 
      }
    })
  },
  //上传头像
  changeFace: function () {
    var me = this;
    wx.chooseImage({
      count: 1, 
      sizeType: ['compressed'], 
      sourceType: ['album'], 
      success: function (res) {
        var tempFilePaths = res.tempFilePaths;
        console.log(tempFilePaths);
        wx.showLoading({
          title: '上传中...',
        })
        var serverUrl = app.serverUrl;
        var userInfo = app.getGloableUserInfo();
        wx.uploadFile({
          url: serverUrl + "/user/uploadFace?userId=" + userInfo.id,
          filePath: tempFilePaths[0],
          name: 'file',
          header: {
            'content-type': 'application/json', //默认值
            "userId": userInfo.id,
            "userToken": userInfo.userToken
          },
          success: function (res) {
            var data = JSON.parse(res.data)
            wx.hideLoading();
            if (data.status == 200) {
              wx.showToast({
                title: '上传成功',
                icon: 'success'
              });
              var imageUrl = data.data;
              me.setData({
                faceUrl: serverUrl + imageUrl
              })
            } else if (data.status == 500) {
              wx.showToast({
                title: '上传失败请重试',
              })
            }
          }
        })
      }
    })
  },
  uploadVideo: function () {
    videoUtil.uplodaVideo()
   },
  followMe: function (e) {
    var me = this;
    var publishId = me.data.publishId;
    var user = app.getGloableUserInfo();
    var userId = user.id;
    var followType = e.currentTarget.dataset.followtype;
    var url = "";
    var serverUrl = app.serverUrl;
    // 0 取关 ,1 关注
    if(followType == "1") {
      url = "/user/beAFans?userId=" + publishId + "&fanId=" + userId;
    } else {
      url = "/user/notAFans?userId=" + publishId + "&fanId=" + userId;
    }

    wx.showLoading({
      title: '请等待',
    })
    wx.request({
      url: serverUrl + url,
      method: "POST",
      header: {
        'content-type': 'application/json', //默认值
        "userId": user.id,
        "userToken": user.userToken
      },
      success: function () {
        wx.hideLoading();
        // 0 取关 ,1 关注
        if (followType == "1") {
          me.setData({
            isFollow: true,
            fansCounts: ++me.data.fansCounts
          })
        } else {
          me.setData({
            isFollow: false,
            fansCounts: --me.data.fansCounts
          })
        }
      }

    })
  },

  //tab栏切换
  doSelectWork: function () {
    var me = this;
    var page = me.data.myVideoPage;
    this.setData({
      isSelectedWork: "video-info-selected",
      isSelectedLike: "",
      isSelectedFollow: "",
      myVideoList: [],
      myVideoPage: 1,
      myVideoTotle: 1,
      myWorkFlag: false,
      myLikesFlag: true,
      myFollowFlag: true
    })
    me.getVideoList(page)
  },
  doSelectLike: function () {
    this.setData({
      isSelectedWork: "",
      isSelectedLike: "video-info-selected",
      isSelectedFollow: "",

      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotle: 1,
      myWorkFlag: true,
      myLikesFlag: false,
      myFollowFlag: true
    })
    var me = this;
    var page = me.data.likeVideoPage;
    console.log(page)
    var pageSize = 6;
    me.getLikeVideo(page);
  },
  doSelectFollow: function () {
    this.setData({
      isSelectedWork: "",
      isSelectedLike: "",
      isSelectedFollow: "video-info-selected",

      FollowList: [],
      FollowPage: 1,
      FollowTotle: 1,

      myWorkFlag: true,
      myLikesFlag: true,
      myFollowFlag: false
    })
    var me = this;
    var page = me.data.FollowPage
    me.getMyFollow(page)
  },
  //我的视频列表API
  getVideoList: function (page) {
    var me = this;
    var userId = me.data.publishId
    var serverUrl = app.serverUrl
    wx.showLoading({
      title: '请等待...',
    });
    wx.request({
      url: serverUrl + '/video/showAll?page=' + page,
      method: "post",
      data: {
        userId: userId
      },
      success: function (res) {
        wx.hideLoading();
        wx.hideNavigationBarLoading();
        wx.stopPullDownRefresh();
        console.log(res.data);
        //判断当前页面是否为首页,如果为首页则清空
        if (myVideoPage == 1) {
          me.setData({
            myVideoList: []
          })
        }
        var myVideoList = res.data.data.rows;
        var newVideoList = me.data.myVideoList;
        var total = res.data.data.total;
        var myVideoPage = res.data.data.page;
        me.setData({
          myVideoList: newVideoList.concat(myVideoList),
          myVideoPage: myVideoPage,
          myVideoTotle: total,
          serverUrl: serverUrl
        })
      }
    })
  },
  //我收藏视频列表API
  getLikeVideo: function(page) { 
    var me = this;
    var serverUrl = app.serverUrl
    var user = app.getGloableUserInfo();
    var userId = me.data.publishId;
    console.log("用户ID=" + userId)
    if (userId == "" || userId == undefined || userId == null || userId == false) {
      userId = user.id;
    }
    console.log("用户ID=" + userId)
    var pageSize = 6;
    wx.showLoading({
      title: '请等待...',
    });
    wx.request({
      url: serverUrl + '/video/showLike?page=' + page 
                     + "&userId=" + userId +"&pageSize=" + pageSize,
      method: "post",
      success: function (res) {
        wx.hideLoading();
        wx.hideNavigationBarLoading();
        wx.stopPullDownRefresh();
        console.log(res.data);
        //判断当前页面是否为首页,如果为首页则清空
        if (likeVideoPage == 1) {
          me.setData({
            likeVideoList: []
          })
        }
        var likeVideoList = res.data.data.rows;
        var newVideoList = me.data.likeVideoList;
        var total = res.data.data.total;
        var likeVideoPage = res.data.data.page;
        me.setData({
          likeVideoList: newVideoList.concat(likeVideoList),
          likeVideoPage: likeVideoPage,
          likeVideoTotle: total,
          serverUrl: serverUrl
        })
      }
    })
  },
  //我关注的人
  getMyFollow: function (page) {
    var serverUrl = app.serverUrl;
    var me = this
    var user = app.getGloableUserInfo();
    var userId = me.data.publishId;
    console.log("用户ID=" + userId)
    if (userId == "" || userId == undefined || userId == null || userId == false) {
      userId = user.id;
    }
    var pageSize = 6;
    wx.showLoading({
      title: '请等待...',
    })
    wx.request({
      url: serverUrl + '/user/queryFollow?userId=' + userId 
        + "&pageSize=" + pageSize + "&page=" + page,
      header: {
        'content-type': 'application/json', //默认值
      },
      method: "post",
      success: function (res) {
        wx.hideLoading();
        wx.hideNavigationBarLoading();
        wx.stopPullDownRefresh();
        console.log(res.data);
        //判断当前页面是否为首页,如果为首页则清空
        if (FollowPage == 1) {
          me.setData({
            FollowList: []
          })
        }
        var FollowList = res.data.data.rows;
        var newVideoList = me.data.FollowList;
        var total = res.data.data.total;
        var FollowPage = res.data.data.page;
        me.setData({
          FollowList: newVideoList.concat(FollowList),
          FollowPage: FollowPage,
          FollowTotle: total,
          serverUrl: serverUrl
        })
        console.log(me.data.FollowList)
      }
    })
  },
  showVideo: function (e) {
    console.log(e);
    var myWorkFlag = this.data.myWorkFlag;
    var myLikesFlag = this.data.myLikesFlag;
    var myFollowFlag = this.data.myFollowFlag;
    if (!myWorkFlag) {
      var videoList = this.data.myVideoList;
    } else if (!myLikesFlag) {
      var videoList = this.data.likeVideoList;
    }

    var arrindex = e.target.dataset.arrindex;
    var videoInfo = JSON.stringify(videoList[arrindex])

    wx.navigateTo({
      url: '../videoInfo/videoInfo?videoInfo=' + videoInfo,
    })
  },
  onReachBottom: function () {
    var myWorkFlag = this.data.myWorkFlag;
    var myLikesFlag = this.data.myLikesFlag;
    var myFollowFlag = this.data.myFollowFlag;
    if (!myWorkFlag) {
      var currentPage = this.data.myVideoPage;
      var totlePage = this.data.myVideoTotle;
      if (currentPage == totlePage) {
        wx.showToast({
          title: '没有视频啦!',
        })
      } else {
        var page = currentPage + 1
        this.getVideoList(page)
      }
    } else if (!myLikesFlag) {
      var currentPage = this.data.likeVideoPage;
      var totlePage = this.data.likeVideoTotle;
      if (currentPage == totlePage) {
        wx.showToast({
          title: '没有视频啦!',
        })
      } else {
        var page = currentPage + 1
        this.getLikeVideo(page)
      }
    }
  }

})
