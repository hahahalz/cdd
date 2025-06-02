package com.example.cdd.Service;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.cdd.Pojo.LoginResult;
import com.example.cdd.Pojo.PlayerInformation;
import com.example.cdd.SQLiteOpenHelper.LoginSQLiteOpenHelper;

public class LoginService extends ViewModel {
    private LoginSQLiteOpenHelper loginDbHelper;

    public LoginService(Context context) {
        loginDbHelper = new LoginSQLiteOpenHelper(context);
    }


    // 注册用户
    public boolean register(String userid, String password) {
        if (!userid.isEmpty() && !password.isEmpty()) {
            PlayerInformation player = new PlayerInformation(userid, password, 0);
            long result = loginDbHelper.register(player);
            return result != -1;
        }
        return false;
    }

    // 用户登录
    public LoginResult login(String userid, String password) {

        return  loginDbHelper.login(userid, password);
    }

    public boolean checkUserExists(String username)
    {
        return loginDbHelper.checkUserExists(username);
    }
}
