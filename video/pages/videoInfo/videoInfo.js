var videoUtil = require('../../utils/videoUtil.js')

const app = getApp()

Page({
  data: {
    cover: "cover",
    videoId: "",
    src: "",
    videoInfo: {},
    placeholder:"说点什么...",
    userLikeVideo:false,
    publishInfo:{},
    serverUrl:"",

    commentsPage:1,
    totalComments:1,
    commentsList:[]
    
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
    me.getCommentsList(1);

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
  },
  showMe: function () {
    var me = this;
    var user = app.getGloableUserInfo();
    var serverUrl = app.serverUrl;
    var videoInfo = me.data.videoInfo;
    wx.showActionSheet({
      itemList: ['下载到本地', '举报用户', '分享到朋友圈', '分享到QQ空间', '分享到微博'],
      success: function (res) {
        console.log(res.tapIndex)
        var index = res.tapIndex;
        if (index == 0) {
          //下载
          wx.downloadFile({
            url: serverUrl + videoInfo.videoPath, //仅为示例，并非真实的资源
            success: function (res) {
              if (res.statusCode === 200) {
                console.log(res.tempFilePath)
                wx.showLoading({
                  title: '请等待...',
                })
                wx.saveVideoToPhotosAlbum({
                  filePath: res.tempFilePath,
                  success(res) {
                    wx.hideLoading();
                    wx.showToast({
                      title: '保存成功!',
                      icon: "success"
                    })
                  }
                })
              }
            }
          })
        } else if (index == 1) {
          //举报
          var publishId = me.data.videoInfo.userId;
          videoInfo = JSON.stringify(me.data.videoInfo)
          var redictUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfo;
          if(user == null || user == undefined || user == "") {
            wx.navigateTo({
              url: '../userLogin/login?redictUrl=' + redictUrl,
            })
          } else {
            var videoId = me.data.videoInfo.id;
            var currentUserId = user.id;
            wx.navigateTo({
              url: '../report/report?videoId='+videoId+"&publishId="+publishId 
                    +"&currentUserId=" + currentUserId,
            })
          }
          

        } else {
          wx.showToast({
            title: '暂未开放接口',
          })
        }
      }
    })
  },
  onShareAppMessage: function (res) {
    if (res.from === 'button') {
      // 来自页面内转发按钮
      console.log(res.target)
    }
    var me = this;
    var videoInfo = JSON.stringify(me.data.videoInfo);
    return {
      title: '视频内容分享',
      path: '/pages/videoInfo/videoInfo?videoInfo=' + videoInfo
    }
  },
  leaveComment: function () {
    var me = this;
    var videoPublishUserId = me.data.videoInfo.userId;
    var user = app.getGloableUserInfo();
    console.log(videoPublishUserId)
    if (videoPublishUserId != user.id) {
      this.setData({
        commentFocus: true
      })
    }
  },
  saveComment: function (e) {
    var me = this;
    var content = e.detail.value;
    var user = app.getGloableUserInfo();
    var serverUrl = app.serverUrl; 
    var videoInfo = JSON.stringify(me.data.videoInfo)
    var redictUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfo;
    //获取回复的评论
    var fatherCommentId = e.currentTarget.dataset.replyfathercommentid;
    var toUserId = e.currentTarget.dataset.replytouserid;
    
    
    if (content.length<=0) {
      wx.showToast({
        title: '再说个字吧!',
      })
      return;
    }
    if (user == null || user == undefined || user == "") {
      wx.navigateTo({
        url: '../userLogin/login?redictUrl=' + redictUrl,
      })
    } else {
      wx.showLoading({
        title: '请等待...',
      })
      wx.request({
        url: serverUrl + "/video/saveComment?fatherCommentId=" + fatherCommentId
          + "&toUserId=" + toUserId ,
        method: "POST",
        header: {
          "userId": user.id,
          "userToken": user.userToken
        },
        data: {
          fromUserId: user.id,
          videoId: me.data.videoInfo.id,
          comment: content
        },
        success: function(res) {
          wx.hideLoading();
          console.log(res);
          me.setData({
            contentValue:"",
            commentsList: []
          })
          me.getCommentsList(1);
        }
      })
    }
  },
  getCommentsList: function (page) {
      // commentsPage: 1,
      // totalComments: 1,
      // commentsList: []
      var me = this;
      var videoId = me.data.videoInfo.id;
      var serverUrl = app.serverUrl
      wx.request({
        url: serverUrl + '/video/getComments?videoId=' + videoId 
              + "&page=" + page + "&pageSize=3",
        method: "POST",
        success: function (res) {
          console.log(res)
          var commentsList = res.data.data.rows;
          var newCommentsList = me.data.commentsList;
          var page = res.data.data.page;
          var total = res.data.data.total;
          me.setData({
            commentsList: newCommentsList.concat(commentsList),
            commentsPage: page,
            totalComments: total,
          })
        }
      })
  },
  onReachBottom:function () {
    var me = this;
    var currentPage = me.data.commentsPage;
    var totalPage = me.data.totalComments;
    if (currentPage == totalPage) {
      return;
    } else {
      var page = currentPage + 1;
      me.getCommentsList(page);
    }
  },
  replyFocus:function (e) {
    console.log(e)
    var fatherCommentId = e.currentTarget.dataset.fathercommentid;
    var toUserId = e.currentTarget.dataset.touserid;
    var toNickName = e.currentTarget.dataset.tonickname
    var user = app.getGloableUserInfo();
    if (e.currentTarget.dataset.touserid != user.id) {
      this.setData({
        placeholder:"回复" + toNickName,
        replyFatherCommentId: fatherCommentId,
        replyToUserId: toUserId,
        commentFocus: true
      })
    }
  }

})