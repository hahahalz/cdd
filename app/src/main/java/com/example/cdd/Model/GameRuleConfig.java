package com.example.cdd.Model;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
public class GameRuleConfig {
    public int RULE_TYPE;
    private CardType cardType;
    public  enum CardType {
        SINGLE, PAIR, THREE_OF_A_KIND, STRAIGHT, SAME_SUIT, THREE_WITH_PAIR, FOUR_WITH_SINGLE, SAME_SUIT_STRAIGHT, FOUR_OF_A_KIND, N;//单牌，对子，三个,顺子，同花五，三带一对，四带一，同花顺，四条，无

        public static CardType getCardType(List<Card> cards, int RULE_TYPE) {
            int cardNum = cards.size();

            if (cardNum == 1) {
                return SINGLE;
            } else if (cardNum == 2) {
                if (cards.get(0).getRank() == cards.get(1).getRank()) {
                    return PAIR;
                } else {
                    return N;
                }
            }//判断是不是对子
            else if (cardNum == 3) {
                Card temp = cards.get(0);
                for (int i = 1; i < 3; i++) {
                    if (temp.getRank() != cards.get(i).getRank()) {
                        return N;
                    }
                }
                return THREE_OF_A_KIND;
            } else if (cardNum == 4) {
                Card temp = cards.get(0);
                for (int i = 1; i < 4; i++) {
                    if (temp.getRank() != cards.get(i).getRank()) {
                        return N;
                    }
                }
                return FOUR_OF_A_KIND;
            }//判断是不是四条
            else if (cardNum == 5) {
                if(cards.get(0).getRank().getValue() == 14 && RULE_TYPE == 0)
                {   if(cards.get(1).getRank().getValue() == 15&&cards.get(2).getRank().getValue() == 3&& cards.get(3).getRank().getValue() == 4&&cards.get(4).getRank().getValue() ==5)
                   { 
                       if (cards.get(0).getSuit() == cards.get(1).getSuit() && cards.get(1).getSuit() == cards.get(2).getSuit() && cards.get(2).getSuit() == cards.get(3).getSuit() && cards.get(3).getSuit() == cards.get(4).getSuit())
                       {
                       return SAME_SUIT_STRAIGHT;//判断同花顺类型
                       }
                    return STRAIGHT;
                  }
                    
                }//当A为第一张牌时
                if (cards.get(0).getRank().getValue() == cards.get(1).getRank().getValue() - 1 && cards.get(1).getRank().getValue() == cards.get(2).getRank().getValue() - 1 && cards.get(2).getRank().getValue() == cards.get(3).getRank().getValue() - 1 && cards.get(3).getRank().getValue() == cards.get(4).getRank().getValue() - 1)
                {
                    for (int i = 0; i < 5; i++) {
                        if (cards.get(i).getRank().getValue() == 15 && RULE_TYPE == 1) {
                            return N;//北方规则2不能参与顺子
                        }
                    }
                    if (cards.get(0).getSuit() == cards.get(1).getSuit() && cards.get(1).getSuit() == cards.get(2).getSuit() && cards.get(2).getSuit() == cards.get(3).getSuit() && cards.get(3).getSuit() == cards.get(4).getSuit()) {
                        return SAME_SUIT_STRAIGHT;//判断同花顺类型
                    }
                    return STRAIGHT;//判断杂顺类型
                } else if (cards.get(0).getSuit() == cards.get(1).getSuit() && cards.get(1).getSuit() == cards.get(2).getSuit() && cards.get(2).getSuit() == cards.get(3).getSuit() && cards.get(3).getSuit() == cards.get(4).getSuit()) {
                    return SAME_SUIT;//判断同花五类型
                } else if (cards.get(0).getRank() == cards.get(1).getRank() && cards.get(1).getRank() == cards.get(2).getRank() && cards.get(3).getRank() == cards.get(4).getRank()) {
                    return THREE_WITH_PAIR;//判断三带一对类型
                } else if (cards.get(0).getRank() == cards.get(1).getRank() && cards.get(1).getRank() == cards.get(2).getRank() && cards.get(2).getRank() == cards.get(3).getRank()) {
                    return FOUR_WITH_SINGLE;//判断四带一类型
                }

            } else {
                int count = cards.size();
                int temp = cards.get(0).getRank().getValue();
                Card c = cards.get(0);
                for (int i = 1; i < count; i++) {
                    if (temp != cards.get(i).getRank().getValue() - 1) {
                        return N;//不满足顺子条件
                    }
                    temp = cards.get(i).getRank().getValue();
                }
                for (int i = 0; i < count; i++) {
                    if (cards.get(i).getRank().getValue() == 15 && RULE_TYPE == 1) {
                        return N;//北方规则2不能参与顺子
                    }
                }
                for (int i = 1; i < count; i++) {
                    if (cards.get(i).getSuit() != c.getSuit()) {
                        return STRAIGHT;//花色不同则是杂顺
                    }
                }
                return SAME_SUIT_STRAIGHT;//都满足则是同花顺

            }
            return N;
        }

    }

    public GameRuleConfig(int temp) {
        RULE_TYPE = temp;
    }

    //传参0代表南方规则，1代表北方规则.

    public boolean isValidPlay(List<Card> playerCards, List<Card> lastPlayedCards,int passtime) {

        playerCards.sort(Comparator.comparingInt((Card c) -> c.getRank().getValue())
                .thenComparingInt(c -> c.getSuit().ordinal()));
        lastPlayedCards.sort(Comparator.comparingInt((Card c) -> c.getRank().getValue())
                .thenComparingInt(c -> c.getSuit().ordinal()));

        if(passtime==3)
        {
            if (CardType.getCardType(playerCards, RULE_TYPE) != cardType.N)
            {
                return true;
            }
        }
        if(cardType.getCardType(playerCards, RULE_TYPE) ==cardType.N)
        {
            return false;
        }
        if (lastPlayedCards.size() == 0) {
            return true;
        }
        if (lastPlayedCards.size() != playerCards.size() && lastPlayedCards.size() != 0) {
            return false;
        }//上一手牌和玩家出的牌的数量不相等，则返回false

        if (lastPlayedCards.size() == 1) {
            if (playerCards.get(0).getRank().getValue() == 15 && lastPlayedCards.get(0).getRank().getValue() == 14 && RULE_TYPE == 0) {
                return false;
            }//南方规则单张2小于A
            else if (playerCards.get(0).getRank().getValue() == 14 && lastPlayedCards.get(0).getRank().getValue() != 14 && RULE_TYPE == 0) {
                return true;
            }//南方规则单张2小于A

            else if (playerCards.get(0).getRank().getValue() > lastPlayedCards.get(0).getRank().getValue()) {
                return true;
            } else if (playerCards.get(0).getRank().getValue() == lastPlayedCards.get(0).getRank().getValue()) {
                if (playerCards.get(0).getSuit().ordinal() > lastPlayedCards.get(0).getSuit().ordinal()) {
                    return true;
                } else {
                    return false;
                }
            }//比较花色
            else {
                return false;
            }

        }
        else if (lastPlayedCards.size() == 2 && cardType.getCardType(playerCards, RULE_TYPE) == cardType.PAIR) {
            Card c1 = lastPlayedCards.get(1);
            Card c2 = playerCards.get(1);
            List<Card> temp1 = new ArrayList<Card>();
            List<Card> temp2 = new ArrayList<Card>();
            temp1.add(c1);
            temp2.add(c2);
            return isValidPlay(temp2, temp1,passtime);
        }//判断对子大小与判断单牌一样
        else if (lastPlayedCards.size() == 3 && cardType.getCardType(playerCards, RULE_TYPE) == cardType.THREE_OF_A_KIND) {
            Card c1 = lastPlayedCards.get(2);
            Card c2 = playerCards.get(2);
            List<Card> temp1 = new ArrayList<Card>();
            List<Card> temp2 = new ArrayList<Card>();
            temp1.add(c1);
            temp2.add(c2);
            return isValidPlay(temp2, temp1,passtime);
        }//判断三个一样的牌的大小
        else if (lastPlayedCards.size() == 4 && cardType.getCardType(playerCards, RULE_TYPE) == cardType.FOUR_OF_A_KIND)
        {
            Card c1 = lastPlayedCards.get(3);
            Card c2 = playerCards.get(3);
            List<Card> temp1 = new ArrayList<Card>();
            List<Card> temp2 = new ArrayList<Card>();
            temp1.add(c1);
            temp2.add(c2);
            return isValidPlay(temp2, temp1,passtime);
        }//判断四条大小
        else if (cardType.getCardType(playerCards, RULE_TYPE) == cardType.STRAIGHT)
        {
            if(cardType.getCardType(lastPlayedCards, RULE_TYPE) == cardType.SAME_SUIT_STRAIGHT)
            {
                return false;
            }
            else
            {
                Card c1 = lastPlayedCards.get(lastPlayedCards.size() - 1);
                Card c2 = playerCards.get(playerCards.size() - 1);
                return c2.compareTo(c1);
            }//当玩家出的牌是顺子的时候

        }//判断顺子的大小
        else if(cardType.getCardType(playerCards, RULE_TYPE) == cardType.SAME_SUIT_STRAIGHT)
        {
            if(cardType.getCardType(lastPlayedCards, RULE_TYPE) == cardType.STRAIGHT)
            {
                return true;
            }
            else
            {
                Card c1 = lastPlayedCards.get(lastPlayedCards.size() - 1);
                Card c2 = playerCards.get(playerCards.size() - 1);
                return c2.compareTo(c1);
            }//当玩家出的牌是同花顺子的时候

        }
        else if (lastPlayedCards.size() == 5 && cardType.getCardType(playerCards, RULE_TYPE) != cardType.N) {
            CardType temp = cardType.getCardType(lastPlayedCards, RULE_TYPE);
            if (temp.ordinal() < cardType.getCardType(playerCards, RULE_TYPE).ordinal()) {
                return true;
            }//按照大小顺序判断牌数量为五的大小
            else if (temp.ordinal() == cardType.getCardType(playerCards, RULE_TYPE).ordinal()) {
                Card c2 = lastPlayedCards.get(lastPlayedCards.size()-1);
                Card c1 = playerCards.get(playerCards.size()-1);
                if (temp == cardType.THREE_WITH_PAIR || temp == cardType.FOUR_WITH_SINGLE) {
                    int last = Card.findMajorityRank(lastPlayedCards);
                    int play = Card.findMajorityRank(playerCards);
                    return play>last;
                }//判断多带一
                else
                {
                      return c1.compareTo(c2);

                }//判断同花五
            }
            else
            {
                return false;
            }
        }
            return false;
    }// 游戏规则配置（检查牌型合法性）
}
