package com.example.cdd.Controller;

import androidx.activity.result.contract.ActivityResultContracts;

import com.example.cdd.Model.Card;
import com.example.cdd.Model.MultiplayerGameManager;

import java.util.List;

public class MutipleController {

    private MultiplayerGameManager multiplayerGameManager;

    MutipleController()
    {
        multiplayerGameManager=new MultiplayerGameManager();
    }

    public boolean playCard(List<Card> cards)
    {
        if(multiplayerGameManager.handlePlayerPlay(cards))
            return true;
        else
            return false;

    }

    public int getWinnerIndex()
    {
        return multiplayerGameManager.checkWinner();
    }

    public List<List<Card>> dealCards()
    {
        return multiplayerGameManager.dealCards();
    }

    public List<List<Card>> nextRound()
    {
        return multiplayerGameManager.selectNextRound();
    }

    public int getNowIndex()
    {
        return multiplayerGameManager.getGameState().getCurrentPlayerIndex();
    }

    public boolean pass()
    {
        return multiplayerGameManager.handlePlayerPass();
    }

    public void quitGame()
    {
        multiplayerGameManager.quitgame();
    }

    public void endGame()
    {
        multiplayerGameManager.endGame();
    }
}
