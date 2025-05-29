package com.example.cdd.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Robot implements Actor{            // 机器人玩家实体

    List<Card> HandCards = new ArrayList<>();
    public List<Card> getHandCards(){
        return Collections.unmodifiableList(new ArrayList<>(HandCards));
    }

    public void setHandCards(List<Card> cards){
        HandCards=cards;
    }
    public void playCards(List<Card> Cards){     //出牌成功或失败（假设已经判断了传入的手牌符合出牌规则）
        for (int i=0;i<Cards.size();i++){
            for (int j=0;j<HandCards.size();j++){
                if (Cards.get(i).equals(HandCards.get(j))){
                    HandCards.remove(j);
                    break;
                }
            }
        }
    }

    public void pass(){
        GameState.getInstance().nextPlayer();
    }
}
