package com.example.cdd.Controller;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.example.cdd.Model.Actor;
import com.example.cdd.Model.Card;
import com.example.cdd.Model.MultiplayerGameManager;
import com.example.cdd.Model.PlayerInformation;
import com.example.cdd.Model.SinglePlayerGameManager;
import com.example.cdd.View.SingleplayerGameFragment;

import java.util.List;

public class GameController extends BaseController<SingleplayerGameFragment,SinglePlayerGameManager>
{
    // 负责游戏主界面（单人/多人）的逻辑


    public GameController()
    {
       initialize();

    }



    public void attachView(SingleplayerGameFragment view) {
        this.view = view;
        onViewAttached();
    }

    public void detachView() {
        onViewDetached();
        this.view = null;
    }

    public void setModel(SinglePlayerGameManager model) {
        this.model = model;
    }

    // 当 View 被绑定时调用
    protected void onViewAttached()
    {

    }

    // 当 View 被解绑时调用
    protected void onViewDetached()
    {

    }




    // 抽象方法，强制子类实现各自的初始化逻辑
    public  List<List<Card>> initialize(int rule, PlayerInformation playerInformation, int levelOfRobot)
    {
        this.model=new SinglePlayerGameManager(rule,playerInformation,levelOfRobot);
        this.view=new SingleplayerGameFragment();
        return this.model.dealCards();

    };

    public  void  initialize()
    {
        this.model=new SinglePlayerGameManager(0,new PlayerInformation(),1);
        this.view=new SingleplayerGameFragment();
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

    public void pass()
    {
        this.model.handlePlayerPass();
    }

    public List<Card>  robotPlayCard()
    {
       return  this.model.handleAIPlay();//null或者打出的牌
    }

    public void quitgame()
    {
        this.model.quitgame();
    }//中途退出游戏，计算扣分，返回玩家信息


    public void endgame()
    {
        this.model.endGame();
    }//一局结束后，选择退出游戏，返回修改分数后的玩家信息，


    public List<List<Card>> selectNextRound()
    {

        return  this.model.selectNextRound();
    }//一轮结束后，选择再来一轮


}
