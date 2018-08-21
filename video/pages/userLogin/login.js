const app = getApp()

Page({
    data: {
      username:""
    },
    doLogin: function (e) {
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
          var serverUrl = app.serverUrl;
          wx.request({
            url: serverUrl + '/login',
            method: 'POST',
            data:{
              username : username,
              password : password
            },
            header: {
              'content-type': 'application/json'//默认值
            },
            success: function(res) {
              console.log(res.data)
              var status = res.data.status;
              //登录成功
              if (status == 200) {
                wx.showToast({
                  title: '登录成功',
                  icon: "success",
                  duration: 2000
                })
                app.userInfo = res.data.data;
                //todu页面跳转

              } else if (status == 500) {
                  wx.showToast({
                    title: res.data.msg,
                    icon: "none",
                    duration: 3000
                  })
              } else {

              }

            }
          })
        }
      },
      resetBtn () {
        wx.redirectTo({
          url: "../userRegist/regist"
        })
      },
      // //注册跳转后,自动填充用户账号,密码
      // onReady: function (options) {
      //   console.log(options.username)
      //   var username = options.username
      //   this.setData({
      //     username: username
      //   }) 
      // },
})