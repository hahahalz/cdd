package com.example.cdd.Model;

import java.util.List;

public class SinglePlayerGameFragment {
    // 管理整个游戏流程、状态、调用规则、AI、网络等（单人模式）
    GameRuleConfig gameRuleConfig;//配置游戏规则
    GameState gameState;//获取游戏状态实例，endgame时要修改
    List<Actor> players;//参与者列表，要改

    Player thePlayer;//获取真人玩家的索引，要改

    Deck deck;


    public SinglePlayerGameFragment(int rule,PlayerInformation playerInformation)
    {
        gameRuleConfig=new GameRuleConfig(rule);
        gameState=GameState.getInstance(players);
        deck=new Deck();
        thePlayer=new Player(playerInformation);
        Robot r1=new Robot(gameRuleConfig);
        players.add(thePlayer);
        players.add(r1);
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

    void endRound()
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

    void selectNextRound()
    {
        //选择下一轮
        gameState.resetRound();
        deck=new Deck();
    }


    void dealCards(Deck deck,List<Actor> players)
    {
        //调用deck发牌
        for(Actor actor:players)
            actor.setHandCards(deck.dealCard());

    }

    Boolean handlePlayerPlay(Actor player, List<Card> cards,List<Card>lastCards)
    {

        //调用GameRuleConfig中的isValidPlay判断出牌是否合理，玩家如果有输入就调用
        if(gameRuleConfig.isValidPlay(cards,lastCards))
        {
            player.playCards(cards);
            if(player.getHandCards().isEmpty())
            {
                gameState.setGameOver(true);
                gameState.setWinner(player);
                endRound();
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    void handleAIPlay(Actor AI)
    {
        //处理AI出牌
        List<Card> aIplay=AI.playCards(gameState.getLastPlayedCards());
        gameState.nextPlayer();
        if(aIplay!=null)
        {
            gameState.setLastPlayedCards(aIplay);
        }
        if(AI.getHandCards().isEmpty())
        {
            gameState.setGameOver(true);
            gameState.setWinner(AI);
            endRound();
        }
        if(gameState.getCurrentPlayer() instanceof Robot)
        {
            handlePlayerPass(gameState.getCurrentPlayer());
        }


    }




    void handlePlayerPass(Actor player)
    {
        //处理过牌
        gameState.nextPlayer();

    }




    GameState getGameState()
    {
        //获取游戏状态
        return gameState;
    }

    void setGameState()
    {
        //设置当前游戏状态

    }
}
