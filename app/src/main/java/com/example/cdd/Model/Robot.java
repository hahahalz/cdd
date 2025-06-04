package com.example.cdd.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import com.example.cdd.Model.GameRuleConfig;
import com.example.cdd.ai_algorithm.Greedy;
import com.example.cdd.ai_algorithm.MCTS_Algorithm;

public class Robot extends Actor{            // 机器人玩家实体

    List<Card> HandCards = new ArrayList<>();
    int level;

    GameRuleConfig gameRuleConfig;

    Greedy greedy;

    MCTS_Algorithm mcts_algorithm;

    public Robot(GameRuleConfig g,int l)
    {
        gameRuleConfig=g;
        level=l;
        greedy = new Greedy();
        mcts_algorithm = new MCTS_Algorithm(g.RULE_TYPE);
    }

    @Override
    public List<Card> playCards(GameState gameState)
    {
        //List<Card> AIPlay=greedy.greedyPlay(HandCards, lastcards, gameRuleConfig, passTime);
        MCTS_Algorithm.MCTS mcts = mcts_algorithm.new MCTS(level);
        List<Card> Cards;
        if(level == 1){
            Cards = greedy.greedyPlay(HandCards,gameState.getLastPlayedCards(),gameRuleConfig,gameState.getPasstime());
        }
        else if(level == 2){
            Cards = mcts.findNextMove(gameState);
        }
        else{
            Cards = mcts.findNextMove(gameState);
        }
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


    Robot copy()
    {
        return new Robot(this.gameRuleConfig,this.level);
    }


}
