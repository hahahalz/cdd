package com.example.cdd.ai_algorithm;

import com.example.cdd.Model.Card;
import com.example.cdd.Model.GameRuleConfig;
import com.example.cdd.Model.GameState;

import java.util.List;

public abstract class Strategy {
    public List<Card> makeDecision(GameState gameState, GameRuleConfig gameRuleConfig){
        return null;
    }
}
