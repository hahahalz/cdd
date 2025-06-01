package com.example.cdd.Model;

import java.util.Objects;
import java.util.Vector;

public class AuthManager {      // 处理用户登录、注册的业务逻辑\
    private Vector<PlayerInformation> playList;       //注册了的用户名单
    boolean isLogin=false;
    public boolean getLoginResult() {
        return isLogin;
    }

    public AuthManager() {
        playList = new Vector<>();
    }

    public boolean register(String userid, String password) {           //1表示注册成功
        if (!Objects.equals(userid, "") &&!Objects.equals(password, "")) {
            for (PlayerInformation playerInformation : playList) {
                String s = playerInformation.getUserID();
                if (Objects.equals(userid, s)) {                       //重名了
                    return false;
                }
            }
            PlayerInformation tmp=new PlayerInformation(userid,password,0);
            playList.add(tmp);
            return true;
        }
        else{
            return false;
        }
    }

    public PlayerInformation login(String userid, String password) {          //1表示登录成功
        for (PlayerInformation playerInformation : playList) {         //遍历比对查找
            String s = playerInformation.getUserID();
            String pwd = playerInformation.getPassword();
            if (Objects.equals(userid, s)) {
                if (Objects.equals(password, pwd)) {
                    isLogin=true;
                    return playerInformation;                            //后续逻辑可在此基础上增加
                }
            }
        }
         isLogin=false;
        return null;
    }
}

