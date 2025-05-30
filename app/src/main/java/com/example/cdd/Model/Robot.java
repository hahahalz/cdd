package com.example.cdd.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import com.example.cdd.Model.GameRuleConfig;

public class Robot implements Actor{            // 机器人玩家实体

    List<Card> HandCards = new ArrayList<>();

    GameRuleConfig gameRuleConfig;

    public Robot(GameRuleConfig g)
    {
        gameRuleConfig=g;
    }

    public List<Card> playCards(List<Card> lastcards)
    {
        return greedyPlay(lastcards);
    }


    public void pass(){
        GameState.getInstance().nextPlayer();
    }
    public List<Card> getHandCards(){
        return Collections.unmodifiableList(new ArrayList<>(HandCards));
    }

    public void setHandCards(List<Card> cards){
        HandCards=cards;
    }
    // 贪心出牌算法（接收游戏配置和状态作为参数）
    public List<Card> greedyPlay(List<Card> lastPlayedCards)
    {

        List<List<Card>> validMoves = generateValidMoves(HandCards, lastPlayedCards, gameRuleConfig);

        if (validMoves.isEmpty()) {
            return null; // 无合法出牌
        }

        // 按牌型优先级和牌值升序排序
        validMoves.sort((a, b) ->
        {
            GameRuleConfig.CardType typeA = GameRuleConfig.CardType.getCardType(a, gameRuleConfig.RULE_TYPE);
            GameRuleConfig.CardType typeB = GameRuleConfig.CardType.getCardType(b, gameRuleConfig.RULE_TYPE);

            // 先比较牌型优先级（值越小优先级越低）
            int typeCompare = Integer.compare(typeA.ordinal(), typeB.ordinal());
            if (typeCompare != 0) return typeCompare;

            // 同牌型比较最小牌值
            Card minA = getMinCard(a, typeA, gameRuleConfig.RULE_TYPE);
            Card minB = getMinCard(b, typeB, gameRuleConfig.RULE_TYPE);
            boolean min=minA.compareTo(minB);// 升序排列，取最小牌型

            return min==true?1:-1;
        }
        );

        return validMoves.get(0); // 返回最小合法牌型
    }

    // 获取牌型中的最小牌（顺子/同花顺取第一张，其他取首张）
    private Card getMinCard(List<Card> cards, GameRuleConfig.CardType type, int ruleType) {
        switch (type) {
            case STRAIGHT:
            case SAME_SUIT_STRAIGHT:
                return cards.get(0); // 顺子/同花顺取第一张牌（最小牌）
            default:
                return cards.stream()
                        .min(Comparator.comparingInt(c -> c.getRank().getValue()))
                        .orElse(null);
        }
    }
    private List<List<Card>> generateValidMoves(List<Card> handCards, List<Card> lastPlayedCards, GameRuleConfig ruleConfig) {
        List<List<Card>> allCombinations = new ArrayList<>();
        int maxCardCount = Math.min(5, handCards.size());

        // 生成1-5张牌的所有组合
        for (int len = 1; len <= maxCardCount; len++) {
            generateCombinations(handCards, len, new ArrayList<>(), 0, allCombinations);
        }

        // 过滤合法牌型并按牌值升序排序
        return allCombinations.stream()
                .filter(cards -> ruleConfig.isValidPlay(cards, lastPlayedCards))
                .sorted((a, b) -> {
                    Card minA = a.stream().min(Comparator.comparingInt(c -> c.getRank().getValue())).orElse(null);
                    Card minB = b.stream().min(Comparator.comparingInt(c -> c.getRank().getValue())).orElse(null);
                    boolean min=minA.compareTo(minB);// 升序排列，取最小牌型

                    return min==true?1:-1;
                })
                .collect(Collectors.toList());
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
