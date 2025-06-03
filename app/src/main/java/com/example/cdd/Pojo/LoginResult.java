package com.example.cdd.Pojo;


public class LoginResult {
    private boolean success;
    private int score;

    public LoginResult(boolean success, int score) {
        this.success = success;
        this.score = score;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getScore() {
        return score;
    }

    public void setSuccess(boolean success1)
    {
        this.success=success1;
    }

    public void setScore(int score1)
    {
        this.score=score1;
    }
}