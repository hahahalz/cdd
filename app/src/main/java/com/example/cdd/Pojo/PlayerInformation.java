package com.example.cdd.Pojo;

public class PlayerInformation {    // 玩家账号信息
    private String userID;
    private String password;
    private int score;              //游戏得分

    private static PlayerInformation thePlayerInformation=new PlayerInformation();

    public PlayerInformation() {
        userID = "";
        password = "";
        score = 0;
    }

    public PlayerInformation(String userid, String password, int score) {
        this.userID = userid;
        this.password = password;
        this.score = score;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userid) {
        this.userID = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public static void setThePlayerInformation(String name,String password,int score)
    {
        thePlayerInformation.userID=name;
        thePlayerInformation.password=password;
        thePlayerInformation.score=score;
    }

    public static PlayerInformation getThePlayerInformation()
    {
        return thePlayerInformation;
    }

    public static void plusThePlayerInformation(int a)
    {
         thePlayerInformation.score+=a;
    }
}
