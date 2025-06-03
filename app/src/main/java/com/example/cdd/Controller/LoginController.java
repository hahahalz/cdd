package com.example.cdd.Controller;

import android.content.Context;

import com.example.cdd.Pojo.LoginResult;
import com.example.cdd.Pojo.PlayerInformation;
import com.example.cdd.Service.UserService;
import com.example.cdd.View.LoginFragment;

public class LoginController  {

    private UserService userService;

    public LoginController(Context context) {

        userService = new UserService(context);
    }

    public boolean checkUserExists(String username)
    {
        return userService.checkUserExists(username);
    }


    public LoginResult login(String userid, String password)
    {
        LoginResult loginResult=this.userService.login(userid, password);
        if(loginResult.isSuccess())
        {
            PlayerInformation.setThePlayerInformation(userid,password,loginResult.getScore());
        }

        return loginResult;
    }

    public boolean registerresult(String userid, String password)
    {
        return this.userService.register(userid, password);
    }
    // 负责登录/注册界面的逻辑
}