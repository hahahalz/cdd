package com.example.cdd.ai_algorithm;

import com.example.cdd.Model.Card;
import com.example.cdd.Model.GameRuleConfig;
import com.example.cdd.Model.GameState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Greedy extends Strategy{

    @Override
    public List<Card> makeDecision(GameState gameState, GameRuleConfig gameRuleConfig){
        return greedyPlay(gameState.getCurrentPlayer().getHandCards(),gameState.getLastPlayedCards(),gameRuleConfig,gameState.getPasstime());
    }
    public List<Card> greedyPlay(List<Card> HandCards,List<Card> lastPlayedCards, GameRuleConfig gameRuleConfig,int passTime) {
        int lastSize = lastPlayedCards.size();
        if (passTime == 3||lastSize==0) {
            // 如果上一轮没有出牌，优先出单张最小牌
            List<List<Card>> singleCardCombinations = generateCombinations(HandCards, 1);
            if (!singleCardCombinations.isEmpty()) {
                singleCardCombinations.sort((a, b) -> {
                    Card minA = a.get(0);
                    Card minB = b.get(0);
                    return minA.compareTo(minB) ? 1 : -1;
                });
                // 确保返回可变列表
                return new ArrayList<>(singleCardCombinations.get(0));
            }
        }
        GameRuleConfig.CardType lastCardType = GameRuleConfig.CardType.getCardType(lastPlayedCards, gameRuleConfig.RULE_TYPE);



        if (lastSize <= 4) {
            // 1-4张牌的情况，找同数量且更大的牌
            List<List<Card>> combinations = generateCombinations(HandCards, lastSize);
            for (List<Card> combination : combinations) {
                if (gameRuleConfig.isValidPlay(combination, lastPlayedCards, GameState.getInstance().getPasstime())) {
                    // 确保返回可变列表
                    return new ArrayList<>(combination);
                }
            }
        } else if (lastSize == 5) {
            // 5张牌的情况，先找同级更大的，再找更高级的
            List<List<Card>> fiveCardCombinations = generateCombinations(HandCards, 5);
            // 先找同级更大的
            for (List<Card> combination : fiveCardCombinations) {
                GameRuleConfig.CardType currentCardType = GameRuleConfig.CardType.getCardType(combination, gameRuleConfig.RULE_TYPE);
                if (currentCardType == lastCardType && gameRuleConfig.isValidPlay(combination, lastPlayedCards, GameState.getInstance().getPasstime())) {
                    // 确保返回可变列表
                    return new ArrayList<>(combination);
                }
            }
            // 再找更高级的
            for (List<Card> combination : fiveCardCombinations) {
                GameRuleConfig.CardType currentCardType = GameRuleConfig.CardType.getCardType(combination, gameRuleConfig.RULE_TYPE);
                if (currentCardType.ordinal() > lastCardType.ordinal() && gameRuleConfig.isValidPlay(combination, lastPlayedCards, GameState.getInstance().getPasstime())) {
                    // 确保返回可变列表
                    return new ArrayList<>(combination);
                }
            }
        }

        return new ArrayList<>(); // 无合法出牌
    }

    // 生成指定长度的所有组合
    private List<List<Card>> generateCombinations(List<Card> handCards, int len) {
        List<List<Card>> allCombinations = new ArrayList<>();
        generateCombinations(handCards, len, new ArrayList<>(), 0, allCombinations);
        return allCombinations;
    }

    private void generateCombinations(List<Card> handCards, int len, List<Card> current, int start, List<List<Card>> result) {
        if (current.size() == len) {
            // 按牌值升序排列
            current.sort(Comparator.comparingInt(c -> c.getRank().getValue()));
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < handCards.size(); i++) {
            current.add(handCards.get(i));
            generateCombinations(handCards, len, current, i + 1, result);
            current.remove(current.size() - 1);
        }
    }
}
