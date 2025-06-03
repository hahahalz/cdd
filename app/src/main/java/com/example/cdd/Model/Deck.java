package com.example.cdd.Model;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.security.SecureRandom;
public class Deck {
    private int TotalNum=52;
    private Card[] cards=new Card[52];//临时数组
    List<Card> cardsList= new ArrayList<Card>();// 牌堆52张牌
    public Deck()
    {

        for(int i=0;i<13;i++)
        {
            for(int j=0;j<4;j++)
            {
                cards[4*i+j]=new Card(i,j);
            }
        }
        for(int i=0;i<52;i++)
        {
            cardsList.add(cards[i]);
        }
    }//牌堆构造函数经行了牌堆的初始化。
    public void shuffle()
    {
        Collections.shuffle(cardsList);//牌堆洗牌
    }
    public List<Card> getDeck()
    {
        return cardsList;
    }
    public void removeCard(Card c)
    {
        cardsList.remove(c);
        TotalNum--;

    }//从牌堆中删除一张牌
    public void addCard(Card c)
    {
        cardsList.add(c);
        TotalNum++;
    }
    public List<Card>dealCard()
    {       SecureRandom secureRandom = new SecureRandom();
            if (TotalNum <13)
           {
               throw new IllegalStateException("牌堆已空，无法发牌"); // 当牌堆为空时抛出异常
           }
            List<Card> hand=new  ArrayList<Card>();
             for(int i=0;i<13;i++)
           {
               int randomNum = secureRandom.nextInt(TotalNum) ;
               Card c=cardsList.get(randomNum);
               removeCard(c);
               hand.add(c);
           }
            // 对hand中的牌按照Card类中定义的大小规则进行排序（从小到大）
            hand.sort((card1, card2) -> {
                if (card1.getRank().getValue() < card2.getRank().getValue()) return -1;
                if (card1.getRank().getValue() > card2.getRank().getValue()) return 1;
                if (card1.getSuit().ordinal() < card2.getSuit().ordinal()) return -1;
                if (card1.getSuit().ordinal() > card2.getSuit().ordinal()) return 1;
                return 0;
            });
             
             return hand;
    }//从牌堆中随机抽取13张牌分发出去


}
