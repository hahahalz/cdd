package com.example.cdd.Controller;

import android.content.Context;

import com.example.cdd.Pojo.LoginResult;
import com.example.cdd.Pojo.PlayerInformation;
import com.example.cdd.Service.LoginService;
import com.example.cdd.View.LoginFragment;

public class LoginController  {

    private  LoginService loginService;

    public LoginController(Context context) {

        loginService = new LoginService(context);
    }

    public boolean checkUserExists(String username)
    {
        return loginService.checkUserExists(username);
    }


    public LoginResult login(String userid, String password)
    {
        LoginResult loginResult=this.loginService.login(userid, password);
        if(loginResult.isSuccess())
        {
            PlayerInformation.setThePlayerInformation(userid,password,loginResult.getScore());
        }

        return loginResult;
    }

    public boolean registerresult(String userid, String password)
    {
        return this.loginService.register(userid, password);
    }
    // 负责登录/注册界面的逻辑
}