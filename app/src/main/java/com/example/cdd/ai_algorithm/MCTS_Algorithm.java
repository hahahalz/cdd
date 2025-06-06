package com.example.cdd.ai_algorithm;

import com.example.cdd.Model.Actor;
import com.example.cdd.Model.Card;
import com.example.cdd.Model.GameRuleConfig;
import com.example.cdd.Model.GameState;
import com.example.cdd.Model.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class MCTS_Algorithm {

    private GameRuleConfig gameRuleConfig;
    private int difficulty;
    //MCTS mcts;

    public MCTS_Algorithm(){
        gameRuleConfig = new GameRuleConfig(1);
        //mcts = new MCTS();
    }

    public MCTS_Algorithm(int rule){
        gameRuleConfig = new GameRuleConfig(rule);
        this.difficulty = difficulty;
        //mcts = new MCTS(difficulty);
    }

    class MCTSNode{
        private GameState gameState;
        private MCTSNode parent;
        private List<Card> move;
        private List<MCTSNode> children;
        private int visitCount;
        private double winScore;

        public MCTSNode(GameState state) {
            this(state, null, null);
        }

        public MCTSNode(GameState state, MCTSNode parent, List<Card> move) {
            this.gameState = state;
            this.parent = parent;
            this.move = move;
            this.children = new ArrayList<>();
            this.visitCount = 0;
            this.winScore = 0;
        }

        public List<MCTSNode> getChildren() {
            return children;
        }

        public List<Card> getMove() {
            return move;
        }

        public GameState getGameState() {
            return gameState;
        }

        public MCTSNode getParent() {
            return parent;
        }

        public int getVisitCount() {
            return visitCount;
        }

        public double getWinScore() {
            return winScore;
        }

        public void incrementVisit() {
            visitCount++;
        }

        public void addScore(double score) {
            winScore += score;
        }

        public boolean isFullyExpanded() {
            if (gameState.isTerminal()) return true;
            return children.size() == getLegalMoves(gameState).size();
        }

        public MCTSNode getRandomChild() {
            int randomIndex = (int) (Math.random() * children.size());
            return children.get(randomIndex);
        }

        public MCTSNode getChildWithMaxScore() {
            return Collections.max(children,
                    Comparator.comparingDouble(c -> c.winScore / c.visitCount +
                            Math.sqrt(2 * Math.log(this.visitCount) / c.visitCount)));
        }
    }

    public class MCTS extends Strategy{
        private static final int SIMULATION_LIMIT = 3000;
        private int TIME_LIMIT_MS;

        public MCTS(){
            TIME_LIMIT_MS = 3000;
        }

        public MCTS(int diffculty) {
            if(diffculty == 2){
                TIME_LIMIT_MS = 1500;
            }
            else{
                TIME_LIMIT_MS = 8000;
            }
        }

        @Override
        public List<Card> makeDecision(GameState gameState, GameRuleConfig gameRuleConfig){
            return findNextMove(gameState);
        }

        public List<Card> findNextMove(GameState initialState) {
            long startTime = System.currentTimeMillis();
            long nowTime = startTime;
            MCTSNode rootNode = new MCTSNode(initialState);

            int simulations = 0;
            while (nowTime - startTime < TIME_LIMIT_MS) {
                // 1. 选择
                MCTSNode promisingNode = selectPromisingNode(rootNode);

                // 2. 扩展
                if (!promisingNode.getGameState().isTerminal()) {
                    expandNode(promisingNode);
                }

                // 3. 模拟
                MCTSNode nodeToExplore = promisingNode;
                if (!promisingNode.getChildren().isEmpty()) {
                    nodeToExplore = promisingNode.getRandomChild();
                }

                double playoutResult = simulateRandomPlayout(nodeToExplore);

                // 4. 反向传播
                backPropagation(nodeToExplore, playoutResult);

                simulations++;

                nowTime = System.currentTimeMillis();
            }

            System.out.println("Performed " + simulations + " simulations");

            // 选择访问次数最多的节点
            MCTSNode bestNode = rootNode.getChildren().stream()
                    .max(Comparator.comparingInt(MCTSNode::getVisitCount))
                    .orElseThrow(() -> new IllegalStateException("No moves available"));

//            Optional<MCTSNode> bestNodeOpt = rootNode.getChildren().stream()
//                    .max(Comparator.comparingInt(MCTSNode::getVisitCount));
//
//            if (bestNodeOpt.isEmpty()) {
//                return new ArrayList<>(); // 或返回一个代表"Pass"的特殊值
//            }
//            MCTSNode bestNode = bestNodeOpt.get();
            return bestNode.getMove();
        }

        private MCTSNode selectPromisingNode(MCTSNode rootNode) {
            MCTSNode node = rootNode;
            while (!node.getChildren().isEmpty()) {
                node = node.getChildWithMaxScore();
            }
            return node;
        }

        private void expandNode(MCTSNode node) {
            List<List<Card>> legalMoves = getLegalMoves(node.getGameState());
            for (List<Card> move : legalMoves) {
                GameState newState = new GameState(node.getGameState());
                applyMove(move,newState);

                MCTSNode childNode = new MCTSNode(newState, node, move);
                node.getChildren().add(childNode);
            }
        }

        private double simulateRandomPlayout(MCTSNode node) {
            GameState tempState = new GameState(node.getGameState());
            Actor originalPlayer = tempState.getCurrentPlayer();


            int cnt = 0;
            // 随机模拟直到游戏结束
            while (!tempState.isTerminal() ) {
                List<List<Card>> legalMoves = getLegalMoves(tempState);
                if (legalMoves.isEmpty()) break;

                // 随机选择一个移动
                //List<Card> randomMove = legalMoves.get(new Random().nextInt(legalMoves.size()));
                List<Card> randomMove = legalMoves.get((int) (Math.random() * legalMoves.size()));
                applyMove(randomMove,tempState);
                cnt++;
            }

            // 计算得分：如果原始玩家赢了得1分，否则得0分
            // 注意：在锄大地中，第一个出完牌的玩家获胜
            // 这里简化处理：如果原始玩家是第一个出完牌的，得1分
            return tempState.isTerminal() &&
                    tempState.getCurrentPlayer() == originalPlayer ? 1.0 : 0.0;
        }

        private void backPropagation(MCTSNode node, double result) {
            MCTSNode tempNode = node;
            while (tempNode != null) {
                tempNode.incrementVisit();
                tempNode.addScore(result);
                tempNode = tempNode.getParent();
            }
        }
    }

    public List<List<Card>> generateAllValidCombinations(List<Card> hand) {
        List<List<Card>> combinations = new ArrayList<>();

        // 按牌值排序（2最大，Ace次之）
        List<Card> sortedHand = new ArrayList<>(hand);
        sortedHand.sort(Comparator.comparingInt(c -> c.getRank().getValue()));

        // 1. 单张
        generateSingleCards(sortedHand, combinations);

        // 2. 对子
        generatePairs(sortedHand, combinations);

        // 3. 三条
        generateTriples(sortedHand, combinations);

        // 4. 顺子（5张或以上连续牌，A-2-3-4-5不算顺子）
        generateStraights(sortedHand, combinations);

        // 5. 同花（5张或以上同花色）
        generateFlushes(sortedHand, combinations);

        // 6. 葫芦（三条加对子）
        generateFullHouses(sortedHand, combinations);

        // 7. 铁支（四条）
        generateQuads(sortedHand, combinations);

        // 8. 同花顺（5张或以上同花色连续牌）
        generateStraightFlushes(sortedHand, combinations);

        return combinations;
    }

    // 辅助方法：生成所有单张
    private void generateSingleCards(List<Card> hand, List<List<Card>> combinations) {
        for (Card card : hand) {
            combinations.add(Collections.singletonList(card));
        }
    }

    // 辅助方法：生成所有对子
    private void generatePairs(List<Card> hand, List<List<Card>> combinations) {
        for (int i = 0; i < hand.size(); i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                if (hand.get(i).getRank() == hand.get(j).getRank()) {
                    combinations.add(Arrays.asList(hand.get(i), hand.get(j)));
                }
            }
        }
    }

    // 辅助方法：生成所有三条
    private void generateTriples(List<Card> hand, List<List<Card>> combinations) {
        for (int i = 0; i < hand.size(); i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                if (hand.get(i).getRank() != hand.get(j).getRank()) continue;
                for (int k = j + 1; k < hand.size(); k++) {
                    if (hand.get(j).getRank() == hand.get(k).getRank()) {
                        combinations.add(Arrays.asList(hand.get(i), hand.get(j), hand.get(k)));
                    }
                }
            }
        }
    }

    // 辅助方法：生成所有顺子（至少5张连续牌值）
    private void generateStraights(List<Card> hand, List<List<Card>> combinations) {
        // 由于2最大且Ace=14，顺子只能是3-4-5-6-7...K(13)-A(14)，不能包含2(15)
        for (int i = 0; i <= hand.size() - 5; i++) {
            List<Card> straight = new ArrayList<>();
            straight.add(hand.get(i));

            int currentRankValue = hand.get(i).getRank().getValue();
            for (int j = i + 1; j < hand.size(); j++) {
                int nextRankValue = hand.get(j).getRank().getValue();

                // 跳过2（因为2不能出现在顺子中）
                if (nextRankValue == 15) continue;

                if (nextRankValue == currentRankValue + 1) {
                    straight.add(hand.get(j));
                    currentRankValue++;

                    if (straight.size() == 5) {
                        combinations.add(new ArrayList<>(straight));
                        break;
                    }
                } else if (nextRankValue > currentRankValue + 1) {
                    break; // 不再连续
                }
            }
        }
    }

    // 辅助方法：生成所有同花（至少5张同花色）
    private void generateFlushes(List<Card> hand, List<List<Card>> combinations) {
        Map<Card.Suit, List<Card>> suitMap = new HashMap<>();
        for (Card card : hand) {
            suitMap.computeIfAbsent(card.getSuit(), k -> new ArrayList<>()).add(card);
        }

        for (List<Card> suitedCards : suitMap.values()) {
            if (suitedCards.size() >= 5) {
                // 生成所有5张及以上的组合
                generateCombinations(suitedCards, 5, combinations);
            }
        }
    }

    // 辅助方法：生成所有葫芦（三条+对子）
    private void generateFullHouses(List<Card> hand, List<List<Card>> combinations) {
        List<List<Card>> triples = new ArrayList<>();
        generateTriples(hand, triples);

        List<List<Card>> pairs = new ArrayList<>();
        generatePairs(hand, pairs);

        // 组合三条和对子（需不同点数）
        for (List<Card> triple : triples) {
            Card.Rank tripleRank = triple.get(0).getRank();
            for (List<Card> pair : pairs) {
                if (pair.get(0).getRank() != tripleRank) {
                    List<Card> fullHouse = new ArrayList<>(triple);
                    fullHouse.addAll(pair);
                    combinations.add(fullHouse);
                }
            }
        }
    }

    // 辅助方法：生成所有铁支（四条）
    private void generateQuads(List<Card> hand, List<List<Card>> combinations) {
        for (int i = 0; i <= hand.size() - 4; i++) {
            if (hand.get(i).getRank() == hand.get(i+1).getRank() &&
                    hand.get(i).getRank() == hand.get(i+2).getRank() &&
                    hand.get(i).getRank() == hand.get(i+3).getRank()) {
                combinations.add(hand.subList(i, i+4));
            }
        }
    }

    // 辅助方法：生成所有同花顺
    private void generateStraightFlushes(List<Card> hand, List<List<Card>> combinations) {
        // 先按花色分组
        Map<Card.Suit, List<Card>> suitMap = new HashMap<>();
        for (Card card : hand) {
            suitMap.computeIfAbsent(card.getSuit(), k -> new ArrayList<>()).add(card);
        }

        // 在每个花色组中查找顺子
        for (List<Card> suitedCards : suitMap.values()) {
            if (suitedCards.size() >= 5) {
                suitedCards.sort(Comparator.comparingInt(c -> c.getRank().getValue()));
                generateStraights(suitedCards, combinations);
            }
        }
    }

    // 辅助方法：生成指定大小的所有组合（通用实现）
    private void generateCombinations(List<Card> cards, int k, List<List<Card>> result) {
        generateCombinations(cards, k, 0, new ArrayList<>(), result);
    }

    private void generateCombinations(List<Card> cards, int k, int start,
                                      List<Card> current, List<List<Card>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < cards.size(); i++) {
            current.add(cards.get(i));
            generateCombinations(cards, k, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    public List<List<Card>> getLegalMoves(GameState gameState) {
        List<List<Card>> moves = new ArrayList<>();
        List<Card> lastCards = gameState.getLastPlayedCards();
        // 如果是新回合或者上一轮获胜玩家出牌
        if (gameState.getLastPlayedCards().isEmpty() || gameState.getPasstime() == 3) {
            // 可以出任意合法牌型
            moves.addAll(generateAllValidCombinations(gameState.getCurrentPlayer().getHandCards()));
            // 也可以选择跳过（仅在不是起始玩家时）
            //moves.add(new ArrayList<>());
        } else {
            // 只能出比上家大的牌型
            List<List<Card>> validCombinations = generateAllValidCombinations(gameState.getCurrentPlayer().getHandCards());
            for (List<Card> combo : validCombinations) {
                if(gameRuleConfig.isValidPlay(combo,gameState.getLastPlayedCards(),gameState.getPasstime())){
                    moves.add(combo);
                }
            }
            // 也可以选择跳过
            moves.add(new ArrayList<>());
        }
        return moves;
    }

    public void applyMove(List<Card> move,GameState gameState) {
        if (move.isEmpty()) {
            // 跳过，轮到下一位玩家
            gameState.PassTimePlus();
        } else {
//            CardMove cardMove = (CardMove) move;
//            List<Card> playedCards = cardMove.getCards();

            // 从手牌中移除打出的牌
            gameState.getCurrentPlayer().playCards(move);
            gameState.setLastPlayedCards(move);
            gameState.setPasstime(0);
            // 检查游戏是否结束
//            if (currentPlayerHand.isEmpty()) {
//                gameOver = true;
//                return;
//            }
        }

//        gameState.getCurrentPlayer().playCards(move);
//        if(!move.isEmpty()) {
//            gameState.setLastPlayedCards(move);
//        }
        // 切换到下一位玩家
        gameState.nextPlayer();
    }

}

