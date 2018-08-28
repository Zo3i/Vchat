const app = getApp()

Page({
    data: {
        reasonType: "请选择原因",
        reportReasonArray: app.reason,
        publishId:"",
        videoId:"",
    },
    onLoad: function (params) {
      var me = this;
      var videoId = params.videoId;
      var publishId = params.publishId
      me.setData({
        videoId: videoId,
        publishId: publishId,
      })
    },
    changeMe:function (e) {
      var me = this;
      var index = e.detail.value;
      var reasonType = app.reason[index];
      me.setData({
        reasonType: reasonType
      })
    },
    submitReport:function (e) {
      var me = this;
      var reasonIndex = e.detail.value.reasonIndex;
      var resonContent = e.detail.value.reasonContent;
      var user = app.getGloableUserInfo();
      var currentUserId = user.id;
      var serverUrl = app.serverUrl;
      if (reasonIndex == null || reasonIndex == undefined || reasonIndex == "") {
        wx.showToast({
          title: '选择举报理由!',
          icon: "none"
        })
        return;
      }
      wx.showLoading({
        title: '请等待...',
      })
      console.log("现在用户" + me.data.publishId)
      wx.request({
        url: serverUrl + '/user/reportUser',
        method: "POST",
        data: {
          dealUserId:me.data.publishId,
          dealVideoId:me.data.videoId,
          title:app.reason[reasonIndex],
          content: resonContent,
          userid:currentUserId,
        },
        header: {
          'content-type': 'application/json', //默认值
          "userId": user.id,
          "userToken": user.userToken
        },
        success: function (res) {
          wx.hideLoading();
          wx.showToast({
            title: res.data.data,
            icon:"success",
            duration:2000
          })
          wx.navigateBack();
        }
      }) 
    }

})
