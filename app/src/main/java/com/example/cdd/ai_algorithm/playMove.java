package com.example.cdd.ai_algorithm;

import com.example.cdd.Model.Card;

import java.util.List;

public class playMove implements Move{
    private final List<Card> cards;

    public playMove(List<Card> cards) {
        this.cards = cards;
    }

    public List<Card> getCards() {
        return cards;
    }

    @Override
    public String toString() {
        return "Play: " + cards;
    }
}
