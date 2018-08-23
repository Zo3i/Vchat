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

    searchContent: ""
  },
  onLoad: function () {
    var me = this;
    var screenWidth = wx.getSystemInfoSync();
    me.setData({
      screenWidth: screenWidth
    });
    //获取当前页数
    var page = me.data.page
    me.getVideoList(page);
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
      me.getVideoList(page);
    }
  },
  //下拉刷新
  onPullDownRefresh: function () {
    wx.showNavigationBarLoading();
    this.getVideoList(1);
  },
  //获取视频API
  getVideoList: function (page) {
    var me = this;
    console.log("currentPage" + me.data.page)
    var serverUrl = app.serverUrl
    wx.showLoading({
      title: '请等待...',
    });

    wx.request({
      url: serverUrl + '/video/showAll?page=' + page,
      method: "post",
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
        var newVideoList = me.data.videoList;
        var total = res.data.data.total;
        me.setData({
          videoList: newVideoList.concat(videoList),
          page: page,
          totalPage: total,
          serverUrl: serverUrl
        })
      }
    })
  },




})
