package com.example.cdd.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GameState {                 // 游戏当前状态（手牌、已出牌、当前轮次等）
    private  List<Actor> players=new ArrayList<>();
    private int currentPlayerIndex;
    private List<Card> cardsOnTable;
    private List<Card> lastPlayedCards;
    private int roundNumber;                  //一共打了几把（有人胜出才算1把）
    private Actor winner;
    private boolean gameOver;

    private int passtime;

    private int roundscore;

    private static GameState instance;    // 单例实例

    private GameState(List<Actor> players) {                 //权限已修改过
        this.players = players;
        this.currentPlayerIndex = 0; // 默认从第一个玩家开始
        this.cardsOnTable = new ArrayList<>();
        this.lastPlayedCards = new ArrayList<>();
        this.roundNumber = 1;
        this.gameOver = false;
        this.winner = null;
        this.roundscore=0;
        this.passtime=3;
    }

    public GameState(GameState state){

        for(Actor actor:state.getPlayers())
        {
            Actor aaa=actor.copy() ;

            List<Card>cards=new ArrayList<>();
            for (Card c:actor.getHandCards())
            {
                cards.add(c);
            }
            aaa.setHandCards(cards);
            this.players.add(aaa);
        }
        this.currentPlayerIndex = state.currentPlayerIndex;
        this.cardsOnTable = new ArrayList<>(state.cardsOnTable);
        this.lastPlayedCards = new ArrayList<>(state.lastPlayedCards);
        this.roundNumber = state.roundNumber;
        this.gameOver = state.gameOver;
        this.winner = null;
        this.passtime = state.passtime;
        this.roundscore = state.roundscore;
    }

    // 静态方法获取单例实例
    public static synchronized GameState getInstance(List<Actor> players) {    //第一次创建实例
        if (instance == null) {
            instance = new GameState(players);
        }
        else
        {
            instance.players = players;
            instance.currentPlayerIndex = 0; // 默认从第一个玩家开始
            instance.cardsOnTable = new ArrayList<>();
            instance.lastPlayedCards = new ArrayList<>();
            instance.roundNumber = 1;
            instance.gameOver = false;
            instance.winner = null;
            instance.roundscore=0;
        }

        return instance;
    }

    // 获取已存在的单例实例（不创建新实例）
    public static GameState getInstance() {
        return instance;
    }

    public List<Actor> getPlayers() {
        return players;
    }

    public int getRoundscore() {
        return roundscore;
    }


    public void setRoundscore(int roundscore) {
        this.roundscore = roundscore;
    }

    public Actor getCurrentPlayer() {
        if (players.isEmpty())
            return null;
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
        this.gameOver=false;
        this.winner=null;
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

    public int getCurrentPlayerIndex()
    {
        return this.currentPlayerIndex;
    }

    public void clearGameState()
    {
        //退出游戏，清理类对象
        this.players = null;
        this.cardsOnTable = null;
        this.lastPlayedCards = null;
        this.winner = null;
    }

    public void quitPunishment()
    {
        this.roundscore--;
    }

    public int getPasstime() {
        return passtime;
    }

    public void setPasstime(int passtime) {
        this.passtime = passtime;
    }

    public void PassTimePlus()
    {
        this.passtime++;
    }

    public void setCurrentPlayerIndex(int a)
    {
        this.currentPlayerIndex=a;
    }

    public boolean isTerminal(){
        if(players.get(currentPlayerIndex).getHandCards().isEmpty()){
            return true;
        }
        else{
            return false;
        }
    }
}