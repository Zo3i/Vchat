const app = getApp()

Page({
    data: {
      username: ""
    },
    doRegist: function (e) {
        var formObject = e.detail.value;
        var username = formObject.username;
        var password = formObject.password;
        //验证账号密码是否为空
        if (username.length == 0 || password.length == 0) {
          wx.showToast({
            title: '用户密码不能为空',
            icon: 'none',
            duration: 3000
          })
        } else {
          //调用后端
          wx.showLoading({
            title: '请等待....',
          })
          var serverUrl = app.serverUrl;
          wx.request({
            url: serverUrl + '/regist',
            method: 'POST',
            data:{
              username : username,
              password : password
            },
            header: {
              'content-type': 'application/json'//默认值
            },
            success: function(res) {
              wx.hideLoading()
              console.log(res.data)
              var status = res.data.status;
              if (status == 200) {
                wx.showToast({
                  title: '用户注册成功',
                  icon: "none",
                  duration: 3000
                })
                //fix 修改原有的全局对象为本地缓存
                app.setGloableUserInfo(res.data.data);
                //注册完跳转到登陆界面
                wx.navigateTo({
                  url: '../userLogin/login?username=' + res.data.data.username,
                }) 
              } else if (status == 500) {
                  wx.showToast({
                    title: res.data.msg,
                    icon: "none",
                    duration: 3000
                  })
              } else {
                wx.showToast({
                  title: "未知错误,请联系管理员",
                  icon: "none",
                  duration: 3000
                })
              }

            }
          })
        }
      },
    goLoginPage () {
      wx.redirectTo({
        url: '../userLogin/login'
      })
    },
})