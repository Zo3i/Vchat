const app = getApp()

Page({
    data: {
        bgmList: [],
        serverUrl: "",
        videoParmas: {}
    },
    //获取视频上传页面传来的参数信息,和bgm列表
    onLoad: function (params) {
      var me = this
      me.setData({
        videoParmas: params
      });
      wx.showLoading({
        title: '请等待...',
      });
      var serverUrl = app.serverUrl
      wx.request({
        url: serverUrl + '/bgm/list',
        header: {
          "content-type" : "application/json"
        },
        method: 'POST',
        success: function(res) {
          console.log(res.data)
          wx.hideLoading();
          if (res.data.status == 200) {
            var bgmList = res.data.data;
            me.setData({
              bgmList: bgmList,
              serverUrl: app.serverUrl
            })
          }
        },
      })
    },
    upload: function (e) {
      var me = this;
      console.log(e)
      var bgmId = e.detail.value.bgmId;
      var desc = e.detail.value.desc;
      console.log("desc"+desc)
      console.log("bgmId"+bgmId)
      //获取视频参数
      var duration = me.data.videoParmas.duration
      var height = me.data.videoParmas.height
      var width = me.data.videoParmas.width
      var tempVideoUrl = me.data.videoParmas.tempVideoUrl
      var tempCoverUrl = me.data.videoParmas.tempCoverUrl


      wx.showLoading({
        title: '上传中...',
      })
      var serverUrl = app.serverUrl;
      var userInfo = app.getGloableUserInfo();
      //上传短视频
      wx.uploadFile({
        url: serverUrl + "/video/uploadVideo",
        formData: {
          userId: userInfo.id,
          bgmId: bgmId,
          desc: desc,
          videoSeconds: duration,
          videoHeight: height,
          videoWidth: width
        },
        filePath: tempVideoUrl,
        name: 'file',
        header: {
          'content-type': 'application/json' //默认值
        },
        success: function (res) {
          var data = JSON.parse(res.data)
          wx.hideLoading();
          if (data.status == 200) {
            console.log("封面上传...")
            var videoId = data.data;
            wx.showLoading({
              title: '上传中...',
            })

            wx.showToast({
              title: '上传成功!',
              duration: 2500,
            })
            wx.navigateBack({
              delta:1,
            })
            // //上传视频封面
            // wx.uploadFile({
            //   url: serverUrl + "/video/uploadVideoCover",
            //   formData: {
            //     userId: app.userInfo.id,
            //     videoId: videoId,
            //   },
            //   filePath: tempCoverUrl,
            //   name: 'file',
            //   header: {
            //     'content-type': 'application/json' //默认值
            //   },
            //   success: function (res) {
            //    var date = JSON.parse(res.data);
            //     wx.hideLoading();
            //     if (data.status == 200) {
            //       console.log(res);
            //       wx.showToast({
            //         title: '上传成功!',
            //         duration: 2500,
            //       })
            //       wx.navigateBack({
            //         delta:1,
            //       })
            //     } else {
            //       wx.showToast({
            //         title: '上传失败,请重试!!',
            //         duration: 2500,
            //       })
            //     }
            //   }
            // })
          } 
        }
      })

      
    }
})

