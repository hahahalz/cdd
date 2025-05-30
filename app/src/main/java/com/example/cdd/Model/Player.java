package com.example.cdd.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player implements Actor{           // 人类玩家实体

    List<Card> HandCards = new ArrayList<>();
    private PlayerInformation playerInformation;        //储存玩家相关信息：ID，得分的变量

    public Player(){      //备用，或者用空的无参构造也行
        playerInformation = new PlayerInformation(" "," ",0);
    }

    public  Player(List<Card> handCards,PlayerInformation playerInformation){
        if (handCards != null){                    //直接将地址给类属性，后续利用类中属性来执行逻辑
            HandCards = handCards;
        }
        this.playerInformation = playerInformation;
    }

    public List<Card> getHandCards() {
        return HandCards;
    }

    public void setHandCards(List<Card> cards){
        HandCards=cards;
    }

    @Override
    public List<Card> playCards(List<Card> Cards){     //出牌成功或失败（假设已经判断了传入的手牌符合出牌规则）
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
    }

    public PlayerInformation getPlayerInformation() {
        return playerInformation;
    }

    public void setPlayerInformation(PlayerInformation playerInformation) {
        this.playerInformation = playerInformation;
    }
}
