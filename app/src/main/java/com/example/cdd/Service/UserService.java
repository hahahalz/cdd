package com.example.cdd.Service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.example.cdd.Pojo.LoginResult;
import com.example.cdd.Pojo.PlayerInformation;
import com.example.cdd.SQLiteOpenHelper.UserSQLiteOpenHelper;



public class UserService extends Service {
    private UserSQLiteOpenHelper UserDbHelper;

    public UserService(Context context)
    {
      super();
      UserDbHelper=new UserSQLiteOpenHelper(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        UserDbHelper = new UserSQLiteOpenHelper(getApplicationContext());
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    // 注册用户
    public boolean register(String userid, String password) {
        if (!userid.isEmpty() && !password.isEmpty()) {
            PlayerInformation player = new PlayerInformation(userid, password, 0);
            long result = this.UserDbHelper.register(player);
            return result != -1;
        }
        return false;
    }

    // 用户登录
    public LoginResult login(String userid, String password) {

        return  this.UserDbHelper.login(userid, password);
    }

    public boolean checkUserExists(String username)
    {
        return this.UserDbHelper.checkUserExists(username);
    }

    public boolean updateUserScore(String userID, int newScore) {
        if (userID.isEmpty())
            return false; // 简单参数校验
        return UserDbHelper.updateScore(userID, newScore);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (UserDbHelper != null) {
            UserDbHelper.close(); // 释放资源
        }
    }

}
