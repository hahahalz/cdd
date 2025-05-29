package com.example.cdd.Model;

import java.util.ArrayList;
import java.util.List;

public interface Actor {        //玩家（人/AI）接口
    List<Card> HandCards = new ArrayList<>();

    List<Card> getHandCards();

    void playCards(List<Card> Cards);

    void pass();
}
