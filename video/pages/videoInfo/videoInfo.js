var videoUtil = require('../../utils/videoUtil.js')

const app = getApp()

Page({
  data: {
    cover: "cover",
    videoId: "",
    src: "",
    videoInfo: {}
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
    me.videoCtx = wx.createVideoContext("myVideo", this)
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

  }

})