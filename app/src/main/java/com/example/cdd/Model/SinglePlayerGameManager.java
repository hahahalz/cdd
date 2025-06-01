package com.example.cdd.Model;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SinglePlayerGameManager extends ViewModel {
    // 管理整个游戏流程、状态、调用规则、AI、网络等（单人模式）
    GameRuleConfig gameRuleConfig;//配置游戏规则
    GameState gameState;//获取游戏状态实例，endgame时要修改
    List<Actor> players;//参与者列表，要改

    Player thePlayer;//获取真人玩家的索引，要改
    Robot r1;
    Robot r2;
    Robot r3;

    Deck deck;



    public SinglePlayerGameManager(int rule, PlayerInformation playerInformation,int levelOfRobot)
    {
        gameRuleConfig=new GameRuleConfig(rule);
        deck=new Deck();
        thePlayer=new Player(playerInformation);

        players=new ArrayList<>();

        players.add(thePlayer);//玩家第一个
        r1=new Robot(gameRuleConfig,levelOfRobot);
        r2=new Robot(gameRuleConfig,levelOfRobot);
        r3=new Robot(gameRuleConfig,levelOfRobot);//定死3个机器人
        players.add(r1);
        players.add(r2);
        players.add(r3);
        //数量定为四
        gameState=GameState.getInstance(players);
    }







    public void endGame()
    {
        //退出游戏,对玩家分数进行处理。只有打完了不玩了的情况调用
        //清空游戏状态、牌堆和每个人的手牌，加分
        endRound();
        int a=thePlayer.getPlayerInformation().getScore();
        thePlayer.getPlayerInformation().setScore(a+gameState.getRoundscore());
        deck=null;
        gameState.clearGameState();

    }

    public void quitgame()
    {
        //中途退出游戏，玩家扣分
        gameState.quitPunishment();
        endGame();
    }

    public Actor checkWinner()
    {
        Actor nowPlay=gameState.getCurrentPlayer();
        if(nowPlay.getHandCards().isEmpty())
        {
            gameState.setWinner(nowPlay);
            return gameState.getCurrentPlayer();
        }
        gameState.nextPlayer();
        return null;
    }


    public void endRound()
    {
        //玩家赢了加回合得分,是另外一个变量
        if(gameState.getCurrentPlayer()== thePlayer)
        {
            int a=gameState.getRoundscore();
            gameState.setRoundscore(a+1);
        }
        //可以返回本回合得分

        //等待页面选择退出游戏还是下一轮，调用selectNextRound
        //selectNextRound();
    }

    public List<List<Card>> selectNextRound()
    {
        endRound();
        //选择下一轮
        gameState.resetRound();
        deck=new Deck();
        return dealCards();
    }


    public List<List<Card>> dealCards()
    {
        //调用deck发牌,要返回二维数组List<List<Card>>
        for(Actor actor:players)
            actor.setHandCards(deck.dealCard());

        List<List<Card>> allCards= new ArrayList<>();
        for(Actor actor:players)
            allCards.add(actor.getHandCards());

        return allCards;
    }

    public Boolean handlePlayerPlay(List<Card> cards)
    {
        if(gameState.isGameOver())
         return false;
        
        //调用GameRuleConfig中的isValidPlay判断出牌是否合理，玩家如果有输入就调用
        if(gameRuleConfig.isValidPlay(cards,gameState.getLastPlayedCards(),gameState.getPasstime()))
        {
            thePlayer.playCards(cards);
            gameState.setLastPlayedCards(cards);
            gameState.nextPlayer();
            gameState.setPasstime(0);
            return true;
        }
        else
        {
            return false;
        }
    }



    public List<Card> handleAIPlay()
    {
        if(gameState.isGameOver())
            return new ArrayList<>();

        //处理AI出牌
        Actor AI=gameState.getCurrentPlayer();
        List<Card> aIplay=AI.playCards(gameState.getLastPlayedCards(),gameState.getPasstime());
        if(!aIplay.isEmpty())
        {
            gameState.setLastPlayedCards(aIplay);
            gameState.nextPlayer();
            gameState.setPasstime(0);
        }
        else
        {
            AI.pass();
        }
        return aIplay;
    }




    public void handlePlayerPass()
    {
        //处理过牌
        gameState.getCurrentPlayer().pass();

    }




    public GameState getGameState()
    {
        //获取游戏状态
        return gameState;
    }

    void setGameState()
    {
        //设置当前游戏状态

    }
}
