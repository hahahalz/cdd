package com.example.cdd.Model;

public class SettingsManager {  // 管理游戏设置，如音效、规则选择等
    private boolean BGM;            //游戏背景音乐，0表示关闭，1表示开启
    private boolean kindsOfRules;   //0表示南方规则，1表示北方规则

    public SettingsManager() {              //无参构造，默认bgm关闭，使用南方规则
        BGM = false;
        kindsOfRules = false;
    }

    public SettingsManager(boolean BGM, boolean kindsOfRules) {
        this.BGM = BGM;
        this.kindsOfRules = kindsOfRules;
    }

    public boolean getBGM() {
        return this.BGM;
    }

    public void setBGM(boolean BGM) {
        this.BGM = BGM;
    }

    public boolean getKindsOfRules() {
        return this.kindsOfRules;
    }

    public void setKindsOfRules(boolean kindsOfRules) {
        this.kindsOfRules = kindsOfRules;
    }
}
