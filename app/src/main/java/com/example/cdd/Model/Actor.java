package com.example.cdd.Model;

import java.util.ArrayList;
import java.util.List;

public interface Actor {        //玩家（人/AI）接口


    List<Card> getHandCards();

    void setHandCards(List<Card> cards);

    List<Card> playCards(List<Card> Cards);

    List<Card> playCards(List<Card> Cards,int passTime);

    void pass();
}
