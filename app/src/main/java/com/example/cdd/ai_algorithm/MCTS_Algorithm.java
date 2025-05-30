package com.example.cdd.ai_algorithm;

import com.example.cdd.Model.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MCTS_Algorithm {
    static class MCTSNode{
        private GameState state;
        private MCTSNode parent;
        private Move move;
        private List<MCTSNode> children;
        private int visitCount;
        private double winScore;

        public MCTSNode(GameState state) {
            this(state, null, null);
        }

        public MCTSNode(GameState state, MCTSNode parent, Move move) {
            this.state = state;
            this.parent = parent;
            this.move = move;
            this.children = new ArrayList<>();
            this.visitCount = 0;
            this.winScore = 0;
        }

        public List<MCTSNode> getChildren() {
            return children;
        }

        public Move getMove() {
            return move;
        }

        public GameState getState() {
            return state;
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
            if (state.getPlayers()[state.getRoundNumber()]) return true;
            return children.size() == state.getLegalMoves().size();
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

    static class MCTS{
        private static final int SIMULATION_LIMIT = 1000;
        private static final int TIME_LIMIT_MS = 2000;

    }
}
