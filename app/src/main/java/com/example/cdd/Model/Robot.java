package com.example.cdd.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import com.example.cdd.Model.GameRuleConfig;
import com.example.cdd.ai_algorithm.Greedy;
import com.example.cdd.ai_algorithm.MCTS_Algorithm;

public class Robot implements Actor{            // 机器人玩家实体

    List<Card> HandCards = new ArrayList<>();
    int level;

    GameRuleConfig gameRuleConfig;

    Greedy greedy;

    MCTS_Algorithm.MCTS mcts;

    public Robot(GameRuleConfig g,int l)
    {
        gameRuleConfig=g;
        level=l;
        greedy = new Greedy();
        mcts = (new MCTS_Algorithm()).new MCTS();
    }

    @Override
    public List<Card> playCards(List<Card> Cards,int passTime)
    {
        //List<Card> AIPlay=greedy.greedyPlay(HandCards, lastcards, gameRuleConfig, passTime);
        //List<Card> AIPlay = mcts.findNextMove();
        for (int i=0;i<Cards.size();i++){
            for (int j=0;j<HandCards.size();j++){
                if (Cards.get(i).equals(HandCards.get(j))){
                    HandCards.remove(j);
                    break;
                }
            }
        }
        return Cards;
    }


    public void pass(){
        GameState.getInstance().nextPlayer();
        GameState.getInstance().PassTimePlus();
    }
    public List<Card> getHandCards(){
        return HandCards;
    }

    public void setHandCards(List<Card> cards){
        HandCards=cards;
    }

    @Override
    public List<Card> playCards(List<Card> Cards) {
        return Collections.emptyList();
    }

    // 贪心出牌算法（接收游戏配置和状态作为参数）

}
