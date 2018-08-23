//app.js
App({
  serverUrl: "http://192.168.222.1:8081",
  userInfo: null,
  setGloableUserInfo: function(user) {
    wx.setStorageSync("userInfo", user)
  },
  getGloableUserInfo: function () {
    return wx.getStorageSync("userInfo")
  }
})