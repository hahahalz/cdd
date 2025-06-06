package com.example.cdd.Model;

import java.util.List;

import androidx.lifecycle.ViewModel;

import com.example.cdd.ai_algorithm.MCTS_Algorithm;

import com.example.cdd.Pojo.PlayerInformation;

import java.util.ArrayList;
import java.util.List;

public class MultiplayerGameManager extends ViewModel {
    // 管理整个游戏流程、状态、调用规则、网络等（多人模式）
    GameRuleConfig gameRuleConfig;//配置游戏规则
    GameState gameState;//获取游戏状态实例，endgame时要修改
    List<Actor> players;//参与者列表，要改

    private Player thePlayer;//房主

    List<List<Card>> allCards;

    Player P2;
    Player P3;
    Player P4;

    Deck deck;

    public MultiplayerGameManager()
    {
        gameRuleConfig=new GameRuleConfig(1);
        deck=new Deck();
        thePlayer=new Player(PlayerInformation.getThePlayerInformation());

        players=new ArrayList<>();



        P2=new Player();
        P3=new Player();
        P4=new Player();
        players.add(thePlayer);
        players.add(P2);
        players.add(P3);
        players.add(P4);
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
        //gameState.clearGameState();


    }

    public void quitgame()
    {
        //中途退出游戏，玩家扣分,其实扣分逻辑要写在这里
        gameState.quitPunishment();
        endGame();
    }


    //一定要调用
    public int checkWinner()
    {

        if(gameState.isGameOver())
        {

            return gameState.getCurrentPlayerIndex();
        }
        else
        {
            gameState.nextPlayer();
        }
         return -1;
    }


    public void endRound()
    {
        //玩家赢了加回合得分,是另外一个变量
        if(gameState.getWinner()== thePlayer)
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
        boolean find=false;

        //调用deck发牌,要返回二维数组List<List<Card>>
        for(Actor actor:players)
            actor.setHandCards(deck.dealCard());

        for(int i=0;i<players.size();i++) {
            for (Card c : players.get(i).getHandCards()) {
                if (c.getRank() == Card.Rank.THREE && c.getSuit() == Card.Suit.Diamond) {
                    gameState.setCurrentPlayerIndex(i);
                    find=true;
                    break;
                }
            }
            if(find)
                break;
        }

        List<List<Card>> allCards= new ArrayList<>();
        for(Actor actor:players)
            allCards.add(actor.getHandCards());

        return allCards;
    }

    //处理每一个玩家出牌
    public Boolean handlePlayerPlay(List<Card> cards)
    {
        if(gameState.isGameOver())
            return false;

        //调用GameRuleConfig中的isValidPlay判断出牌是否合理，玩家如果有输入就调用
        if(gameRuleConfig.isValidPlay(cards,gameState.getLastPlayedCards(),gameState.getPasstime()))
        {
            Actor nowplay= gameState.getCurrentPlayer();
            nowplay.playCards(cards);
            gameState.setLastPlayedCards(cards);
            gameState.nextPlayer();
            gameState.setPasstime(0);
            if(nowplay.getHandCards().isEmpty())
            {
                gameState.setWinner(nowplay);
                gameState.setGameOver(true);
            }
            return true;
        }
        else
        {
            return false;
        }
    }





    public boolean handlePlayerPass()
    {
        //处理过牌
        if(gameState.getLastPlayedCards().isEmpty()||gameState.getPasstime()==3)
            return false;
        else
        {
            gameState.getCurrentPlayer().pass();
            return true;
        }

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
