const app = getApp()

Page({
  data: {
    // 用于分页的属性
    totalPage: 1,
    page:1,
    videoList:[],
    currentPage:1,

    screenWidth: 350,
    serverUrl: "",
    seachContent: ""
  },
  onLoad: function (params) {
    var me = this;
    console.log("手屏幕宽度" + wx.getSystemInfoSync().windowWidth)
    var screenWidth = wx.getSystemInfoSync().windowWidth;
    me.setData({
      screenWidth: screenWidth
    });
    //搜索结果
    var seachContent = params.search;
    me.setData({
      seachContent: seachContent
    })
    console.log(params)
    var isSave = params.isSave;
    if (isSave == null || isSave == "" || isSave == undefined) {
      isSave = 0;
    }

    //获取当前页数
    var page = me.data.page
    me.getVideoList(page, isSave);
  },
  //上拉刷新
  onReachBottom: function () {
    var me = this;
    var currentPage = me.data.page;
    var totlePage = me.data.totalPage;
    console.log("总页数:"+totlePage)
    //判断当前的页数是否达到总页数
    if (currentPage == totlePage) {
      wx.showToast({
        title: '已经没有视频了...',
        icon: "none",
        duration: 2500
      })
    } else{
      var page = currentPage + 1;
      me.getVideoList(page, 0);
    }
  },
  //下拉刷新
  onPullDownRefresh: function () {
    wx.showNavigationBarLoading();
    this.getVideoList(1, 0);
  },
  //获取视频API
  getVideoList: function (page, isSave) {
    var me = this;
    console.log("currentPage" + me.data.page)
    var serverUrl = app.serverUrl
    //获取热搜词
    var seachContent = me.data.seachContent;
    wx.showLoading({
      title: '请等待...',
    });

    wx.request({
      url: serverUrl + '/video/showAll?page=' + page 
                     + "&isSave=" + isSave,
      method: "post",
      data: {
        videoDesc: seachContent
      },
      success: function (res) {
        wx.hideLoading();
        wx.hideNavigationBarLoading();
        wx.stopPullDownRefresh();
        console.log(res.data);
        //判断当前页面是否为首页,如果为首页则清空
        if (page == 1) {
          me.setData({
            videoList: []
          })
        }
        var videoList = res.data.data.rows;
        console.log(videoList)
        if (videoList.length == 0) {
          console.log("啥也没有")
          wx.showToast({
            title: '啥也没有!',
            duration: 2000,
          })
          setTimeout(
            function () {
              wx.navigateTo({
                url: '../index/index',
              })
            },2000
          )
          
        }
        var newVideoList = me.data.videoList;
        var total = res.data.data.total;
        me.setData({
          videoList: newVideoList.concat(videoList),
          page: page,
          totalPage: total,
          serverUrl: serverUrl,
        })
      }
    })
  },
//跳转到视频播放
  showVideoInfo: function (e) {
    var me = this;
    var videoList = me.data.videoList
    var videoIndex = e.target.dataset.arrindex;
    console.log(videoIndex)
    var videoInfo = JSON.stringify(videoList[videoIndex])
    wx.navigateTo({
      url: '../videoInfo/videoInfo?videoInfo=' + videoInfo,
    })
  }



})
