//app.js
App({
  serverUrl: "http://192.168.222.1:8081",
  userInfo: null,
  setGloableUserInfo: function(user) {
    wx.setStorageSync("userInfo", user)
  },
  getGloableUserInfo: function () {
    return wx.getStorageSync("userInfo")
  },
  reason:[
    "色情低俗",
    "政治敏感",
    "涉嫌诈骗",
    "广告垃圾",
    "诱导分享",
    "引起不适",
    "过分暴力",
    "违法乱纪",
    "其他原因"
  ]
})