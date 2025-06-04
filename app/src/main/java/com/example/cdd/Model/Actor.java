package com.example.cdd.Model;

import java.util.ArrayList;
import java.util.List;

public class Actor {        //玩家（人/AI）接口


    Actor copy(){
        return null;
    };

    public List<Card> getHandCards() {
        return null;
    }

    void setHandCards(List<Card> cards) {

    }

    public List<Card> playCards(List<Card> Cards) {
        return null;
    }

    public List<Card> playCards(GameState gameState) {
        return null;
    }

    List<Card> playCards(List<Card> Cards, int passTime) {
        return null;
    }

    //List<Card> playCards(GameState gameState,List<Card> Cards,int passTime);

    void pass() {

    }


}
