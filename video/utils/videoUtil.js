 function uplodaVideo() {
  var me = this
  wx.chooseVideo({
    sourceType: ['album'],
    success: function(res) {
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
          url: '../chooseBgm/chooseBgm?duration=' + duration +
            '&height=' + height +
            '&width=' + width +
            '&tempVideoUrl=' + tempVideoUrl +
            '&tempCoverUrl=' + tempCoverUrl
        })
      }
    }
  })
}

//videoUpload
module.exports = {
  uplodaVideo: uplodaVideo
}