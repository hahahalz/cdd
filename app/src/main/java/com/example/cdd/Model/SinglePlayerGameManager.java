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



    public SinglePlayerGameManager(int rule, PlayerInformation playerInformation,List<Integer> levelOfRobot )
    {
        gameRuleConfig=new GameRuleConfig(rule);
        deck=new Deck();
        thePlayer=new Player(playerInformation);
        players.add(thePlayer);//玩家第一个
        Robot r1=new Robot(gameRuleConfig,levelOfRobot.get(0));
        Robot r2=new Robot(gameRuleConfig,levelOfRobot.get(1));
        Robot r3=new Robot(gameRuleConfig,levelOfRobot.get(2));//定死3个机器人
        players.add(r1);
        players.add(r2);
        players.add(r3);
        //数量定为四

        gameState=GameState.getInstance(players);
    }




    void PlayingGame()
    {
        //具体游戏过程
        while(!gameState.isGameOver())
        {
            //根据页面的指令选择是过牌还是出牌
            //handlePlayerPass();or handlePlayerPlay
            //handleAIPlay();
            //
        }

        endGame();


//jiaogeiqitahanshu

    }


    void endGame()
    {
        //退出游戏,对玩家分数进行处理。只有打完了不玩了的情况调用
        //清空游戏状态、牌堆和每个人的手牌，加分
        //在外面取消对这个类的引用

    }

    void quitgame()
    {
        //中途退出游戏，玩家扣分
        endGame();
        int a=thePlayer.getPlayerInformation().getScore();
        thePlayer.getPlayerInformation().setScore(a-1);

    }

    public Actor checkWinner()
    {
        Actor nowPlay=gameState.getCurrentPlayer();
        if(nowPlay.getHandCards().isEmpty())
        {
            gameState.setWinner(nowPlay);
            return gameState.getCurrentPlayer();
        }
        return null;
    }


    public void endRound()
    {
        //玩家赢了加分
        if(gameState.getWinner() instanceof Player)
        {
            int a=thePlayer.getPlayerInformation().getScore();
            thePlayer.getPlayerInformation().setScore(a+1);
        }
        //等待页面选择退出游戏还是下一轮，调用selectNextRound
        //selectNextRound();
    }

    public void selectNextRound()
    {
        //选择下一轮
        gameState.resetRound();
        deck=new Deck();
    }


    List<List<Card>> dealCards(Deck deck,List<Actor> players)
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

        //调用GameRuleConfig中的isValidPlay判断出牌是否合理，玩家如果有输入就调用
        if(gameRuleConfig.isValidPlay(cards,gameState.getLastPlayedCards()))
        {
            thePlayer.playCards(cards);
            return true;
        }
        else
        {
            return false;
        }
    }



    public void handleAIPlay()
    {
        //处理AI出牌
        Robot AI=gameState.getCurrentPlayer();
        List<Card> aIplay=AI.playCards(gameState.getLastPlayedCards());
        gameState.nextPlayer();
        if(aIplay!=null)
        {
            gameState.setLastPlayedCards(aIplay);
            if(AI.getHandCards().isEmpty())
            {
                gameState.setGameOver(true);
                gameState.setWinner(AI);
                endRound();
            }
        }

        else if(gameState.getCurrentPlayer() instanceof Robot)
        {
            handleAIPass();
        }

    }




    public void handlePlayerPass()
    {
        //处理过牌
        gameState.nextPlayer();

    }

    public void handleAIPass()
    {
        //处理过牌
        gameState.nextPlayer();

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
