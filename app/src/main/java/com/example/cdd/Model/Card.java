package com.example.cdd.Model;

import androidx.annotation.NonNull;

public class Card {
    public enum Rank {
        THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10),
        JACK(11), QUEEN(12), KING(13), ACE(14), TWO(15); // 2 最大

        private final int value;
        Rank(int value) { this.value = value; }//构造函数通过int进行赋值
        public int getValue() { return value; }
    }//定义牌值，并且赋值便于后面比大小）
    public enum Suit { Club, Diamond, Heart, Spade }//定义花色梅花，方块，红桃，黑桃

    private Rank rank;  // 使用枚举类型
    private Suit suit;

    // 构造函数
    public Card(int r, int s) {
        Rank rank = Rank.values()[r];
        Suit suit = Suit.values()[s];
        setRank(rank);
        setSuit(suit);
    }
    // set/get方法
    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public void setSuit(Suit suit) {
        this.suit = suit;
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }
    @NonNull
    public String toString() {
        return suit + " of " + rank;
    }
    public boolean compareTo(Card c)
    {
        if(this.rank.getValue()>c.getRank().getValue())
        {
            return true;
        }//当牌值比c大时返回true
        else if(this.rank.getValue()<c.getRank().getValue())
        {
            return false;
        }//当牌值比c小时返回false
        else if(this.rank.getValue()==c.getRank().getValue())
        {
            if(this.suit.ordinal()>c.getSuit().ordinal())
            {
                return true;
            }
            else if(this.suit.ordinal()<c.getSuit().ordinal())
            {
                return false;
            }
            else
            {
                return false;
            }
        }//当牌值相等时，比较花色
    return false;
    }//判断两个牌的大小
    public boolean equals(Card c)
    {
        if(this.rank.getValue()==c.getRank().getValue()&&this.suit.ordinal()==c.getSuit().ordinal())
        {
            return true;
        }
        else
        {
            return false;
        }
    }//重写equals方法

}
