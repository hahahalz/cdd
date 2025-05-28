package com.example.cdd.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GameState {                 // 游戏当前状态（手牌、已出牌、当前轮次等）
    private final List<Actor> players;
    private int currentPlayerIndex;
    private List<Card> cardsOnTable;
    private List<Card> lastPlayedCards;
    private int roundNumber;                  //一共打了几把（有人胜出才算1把）
    private Actor winner;
    private boolean gameOver;

    public GameState(List<Actor> players) {
        this.players = Objects.requireNonNull(players);
        this.currentPlayerIndex = 0; // 默认从第一个玩家开始
        this.cardsOnTable = new ArrayList<>();
        this.lastPlayedCards = new ArrayList<>();
        this.roundNumber = 1;
        this.gameOver = false;
        this.winner = null;
    }

    public List<Actor> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Actor getCurrentPlayer() {
        if (players.isEmpty()) return null;
        return players.get(currentPlayerIndex);
    }

    public List<Card> getCardsOnTable() {
        return Collections.unmodifiableList(cardsOnTable);
    }

    public List<Card> getLastPlayedCards() {
        return Collections.unmodifiableList(lastPlayedCards);
    }

    public void setLastPlayedCards(List<Card> cards) {
        this.lastPlayedCards = new ArrayList<>(Objects.requireNonNull(cards));
        this.cardsOnTable.addAll(cards); // 将牌添加到桌面上
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public void resetRound() {
        this.cardsOnTable.clear();
        this.lastPlayedCards.clear();
        this.roundNumber++;
        // 重置跳过状态等（如果需要）
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public Actor getWinner() {
        return winner;
    }

    public void setWinner(Actor winner) {
        this.winner = winner;
    }
}