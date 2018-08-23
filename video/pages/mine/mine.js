var videoUtil = require('../../utils/videoUtil.js')

const app = getApp()

Page({
  data: {
    faceUrl: "../resource/images/noneface.png",
    isMe: true,
    isFollow: false, 
  },
  //页面加载获取用户数据
  onLoad: function () {
    var me = this;
    var user = app.getGloableUserInfo();
    wx.showLoading({
      title: '请等待..',
    })
    var serverUrl = app.serverUrl
    wx.request({
      url: serverUrl + '/user/query?userId=' + user.id,
      method: "POST",
      herder:{
        'content-type' : 'application/json'
      },
      success: function (res) {
        console.log(res.data)
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
            nickname: userInfo.nickname
          })
        }
      }
    })

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
        'content-type' : 'application/json' //默认值
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
            'content-type': 'application/json' //默认值
          },
          success: function (res) {
            var data = JSON.parse(res.data)
            console.log(data);
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
            } else if (data.ststus == 500) {
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
    var me = this
    wx.chooseVideo({
      sourceType: ['album'],
      success: function (res) {
        console.log(res)
        var duration = res.duration
        var height = res.height
        var width = res.width
        var tempVideoUrl = res.tempFilePath
        var tempCoverUrl = res.thumbTempFilePath

        if (duration > 110000) {
          wx.showToast({
            title: '视频长度不能超过10秒',
            icon: "none",
            duration: 2500
          })
        } else if (duration < 1) {
          wx.showToast({
            title: '视频长度太短啦~',
            icon: "none",
            duration: 2500
          })
        } else {
          console.log("跳转")
          wx.navigateTo({
            url: '../chooseBgm/chooseBgm?duration=' + duration
              + '&height=' + height
              + '&width=' + width
              + '&tempVideoUrl=' + tempVideoUrl
              + '&tempCoverUrl=' + tempCoverUrl
          })
        }
      }
    })
  }
})
