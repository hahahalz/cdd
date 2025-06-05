package com.example.cdd.Controller;

import android.content.Context;

import com.example.cdd.Model.Actor;
import com.example.cdd.Model.Card;
import com.example.cdd.Model.SinglePlayerGameManager;
import com.example.cdd.Pojo.PlayerInformation;
import com.example.cdd.Service.UserService;
import com.example.cdd.View.SingleplayerGameFragment;

import java.util.List;

public class GameController
{
    // 负责游戏主界面（单人/多人）的逻辑
    private UserService scoreService;
    private SinglePlayerGameManager model;

    public GameController(Context context)
    {

       scoreService=new UserService(context);//这个是对后端的初始化

    }



    public void setModel(SinglePlayerGameManager model) {
        this.model = model;
    }

    // 当 View 被绑定时调用



    // 抽象方法，强制子类实现各自的初始化逻辑
    public  List<List<Card>> initialize(int rule, int levelOfRobot)
    {
        this.model=new SinglePlayerGameManager(rule,levelOfRobot);
        return this.model.dealCards();

    };

    public  void  initialize()
    {
        this.model=new SinglePlayerGameManager(0,1);

    };


    // 抽象方法，强制子类实现各自的清理逻辑
    public void onDestroy()
    {

    };

    //具体给gameManager做的
    int getCurrentTurn()
    {
       return this.model.getGameState().getCurrentPlayerIndex();
    }

    public Actor CheckWinner()
    {
        //
        return this.model.checkWinner();
    }

    public Boolean playHandCard(List<Card> list)
    {
       return this.model.handlePlayerPlay(list);
    }

    public boolean pass()
    {
        return this.model.handlePlayerPass();
    }

    public List<Card>  robotPlayCard()
    {
       return  this.model.handleAIPlay();//null或者打出的牌
    }

    public void quitgame()
    {
        this.model.quitgame();
        System.out.println(this.scoreService.updateUserScore(PlayerInformation.getThePlayerInformation().getUserID(), PlayerInformation.getThePlayerInformation().getScore()));
    }//中途退出游戏，计算扣分，返回玩家信息


    public void endgame()
    {
        this.model.endGame();
        System.out.println(this.scoreService.updateUserScore(PlayerInformation.getThePlayerInformation().getUserID(), PlayerInformation.getThePlayerInformation().getScore()));
    }//一局结束后，选择退出游戏，返回修改分数后的玩家信息，


    public List<List<Card>> selectNextRound()
    {

        return  this.model.selectNextRound();
    }//一轮结束后，选择再来一轮

    public int getCurrentPlayerIndex()
    {
        return  this.model.getGameState().getCurrentPlayerIndex();
    }
}
