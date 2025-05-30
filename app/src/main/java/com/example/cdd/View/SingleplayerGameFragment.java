package com.example.cdd.View;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cdd.R;

import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

import java.util.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleplayerGameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
// 人机对战游戏主界面
public class SingleplayerGameFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // 游戏相关控件
    private ArrayList<Button> playerCardsImage; //显示玩家手中的扑克牌
    private TextView whoPlay; //谁出的牌
    private TextView whoseTurn; //轮到谁出牌
    private TextView computer1CardsText; //显示机器人剩余的牌数
    private Button playButton; //出牌
    private Button passButton; //过牌

    // 游戏数据
    private ArrayList<Integer> playerCards;
    private ArrayList<Integer> computer1Cards;
    private ArrayList<Integer> currentPlayCards;
    private int currentPlayer;
    public static final int PLAYER = 0, COMPUTER1 = 1;

    public SingleplayerGameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SingleplayerGameFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SingleplayerGameFragment newInstance(String param1, String param2) {
        SingleplayerGameFragment fragment = new SingleplayerGameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(layoutId(), container, false);
        initView(view);
        initData(context);
        return view;
    }

    //@Override
    protected int layoutId() {
        return R.layout.fragment_singleplayer_game;
    }

    //@Override
    protected void initView(View view) {
        // 初始化玩家和电脑的牌面显示控件
        playerCardsImage = new ArrayList<>();
        playerCardsImage.add(view.findViewById(R.id.cluba));
        playerCardsImage.add(view.findViewById(R.id.club2));
        playerCardsImage.add(view.findViewById(R.id.club3));
        playerCardsImage.add(view.findViewById(R.id.club4));
        playerCardsImage.add(view.findViewById(R.id.club5));
        playerCardsImage.add(view.findViewById(R.id.club6));
        playerCardsImage.add(view.findViewById(R.id.club7));
        playerCardsImage.add(view.findViewById(R.id.club8));
        playerCardsImage.add(view.findViewById(R.id.club9));
        playerCardsImage.add(view.findViewById(R.id.club10));
        playerCardsImage.add(view.findViewById(R.id.clubj));
        playerCardsImage.add(view.findViewById(R.id.clubq));
        playerCardsImage.add(view.findViewById(R.id.clubk));

        playerCardsImage.add(view.findViewById(R.id.hearta));
        playerCardsImage.add(view.findViewById(R.id.heart2));
        playerCardsImage.add(view.findViewById(R.id.heart3));
        playerCardsImage.add(view.findViewById(R.id.heart4));
        playerCardsImage.add(view.findViewById(R.id.heart5));
        playerCardsImage.add(view.findViewById(R.id.heart6));
        playerCardsImage.add(view.findViewById(R.id.heart7));
        playerCardsImage.add(view.findViewById(R.id.heart8));
        playerCardsImage.add(view.findViewById(R.id.heart9));
        playerCardsImage.add(view.findViewById(R.id.heart10));
        playerCardsImage.add(view.findViewById(R.id.heartj));
        playerCardsImage.add(view.findViewById(R.id.heartq));
        playerCardsImage.add(view.findViewById(R.id.heartk));

        playerCardsImage.add(view.findViewById(R.id.diamonda));
        playerCardsImage.add(view.findViewById(R.id.diamond2));
        playerCardsImage.add(view.findViewById(R.id.diamond3));
        playerCardsImage.add(view.findViewById(R.id.diamond4));
        playerCardsImage.add(view.findViewById(R.id.diamond5));
        playerCardsImage.add(view.findViewById(R.id.diamond6));
        playerCardsImage.add(view.findViewById(R.id.diamond7));
        playerCardsImage.add(view.findViewById(R.id.diamond8));
        playerCardsImage.add(view.findViewById(R.id.diamond9));
        playerCardsImage.add(view.findViewById(R.id.diamond10));
        playerCardsImage.add(view.findViewById(R.id.diamondj));
        playerCardsImage.add(view.findViewById(R.id.diamondq));
        playerCardsImage.add(view.findViewById(R.id.diamondk));

        playerCardsImage.add(view.findViewById(R.id.spadea));
        playerCardsImage.add(view.findViewById(R.id.spade2));
        playerCardsImage.add(view.findViewById(R.id.spade3));
        playerCardsImage.add(view.findViewById(R.id.spade4));
        playerCardsImage.add(view.findViewById(R.id.spade5));
        playerCardsImage.add(view.findViewById(R.id.spade6));
        playerCardsImage.add(view.findViewById(R.id.spade7));
        playerCardsImage.add(view.findViewById(R.id.spade8));
        playerCardsImage.add(view.findViewById(R.id.spade9));
        playerCardsImage.add(view.findViewById(R.id.spade10));
        playerCardsImage.add(view.findViewById(R.id.spadej));
        playerCardsImage.add(view.findViewById(R.id.spadeq));
        playerCardsImage.add(view.findViewById(R.id.spadek));

        //初始化机器人剩余牌数显示文本
        computer1CardsText = view.findViewById(R.id.computer1_cards_text);

        //初始化出牌者
        whoPlay = view.findViewById(R.id.who_play);

        //初始化该轮到谁
        whoseTurn = view.findViewById(R.id.whose_turn);

        // 初始化操作按钮
        playButton = view.findViewById(R.id.play_button);
        passButton = view.findViewById(R.id.pass_button);

        // 设置按钮点击事件
        playButton.setOnClickListener(v -> handlePlayCards());
        passButton.setOnClickListener(v -> handlePass());
        for (int i = 0; i < 52; ++i) {
            final int tmp = i;
            playerCardsImage.get(i).setOnClickListener(v -> handleCardClicked(tmp));
        }
    }

    @Override
    protected void initData(Context context) {
        playerCards = new ArrayList<>();
        computer1Cards = new ArrayList<>();
        currentPlayCards = new ArrayList<>();

        currentPlayer = PLAYER;

        ArrayList<Integer> cards = shuffleCards();
        dealCards(cards);

        //初始化UI界面
        whoPlay.setText("等待你出牌");
        whoseTurn.setText("目前轮到你出牌");
        computer1CardsText.setText("机器人剩余26张牌");
        playButton.setEnabled(true);
        passButton.setEnabled(true);
        for (int i = 0; i < 52; ++i) {
            if (playerCards.contains(i)) {
                playerCardsImage.get(i).setVisibility(View.VISIBLE);
                playerCardsImage.get(i).setEnabled(true);
            }
            else playerCardsImage.get(i).setVisibility(View.GONE);
        }
    }

    //洗牌
    private ArrayList<Integer> shuffleCards() {
        ArrayList<Integer> cards = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 52; ++i)
            cards.add(i);
        for (int i = 1; i < 52; ++i) {
            int random_int = random.nextInt(i);
            int tmp = cards.get(i);
            cards.set(i, cards.get(random_int));
            cards.set(random_int, tmp);
        }
        return cards;
    }

    String currentPlayCardsToString() {
        StringBuilder s = new StringBuilder("\n");
        for (Integer card : currentPlayCards) {
            if (card >= 0 && card <= 12) {
                s.append("♣");
                if (card == 0)
                    s.append("A");
                else if (card == 10)
                    s.append("J");
                else if (card == 11)
                    s.append("Q");
                else if (card == 12)
                    s.append("K");
                else s.append(card + 1);
            }
            else if (card >= 13 && card <= 25) {
                s.append("♥");
                if (card == 13)
                    s.append("A");
                else if (card == 23)
                    s.append("J");
                else if (card == 24)
                    s.append("Q");
                else if (card == 25)
                    s.append("K");
                else s.append(card - 12);
            }
            else if (card >= 26 && card <= 38) {
                s.append("♦");
                if (card == 26)
                    s.append("A");
                else if (card == 36)
                    s.append("J");
                else if (card == 37)
                    s.append("Q");
                else if (card == 38)
                    s.append("K");
                else s.append(card - 25);
            }
            else {
                s.append("♠");
                if (card == 39)
                    s.append("A");
                else if (card == 49)
                    s.append("J");
                else if (card == 50)
                    s.append("Q");
                else if (card == 51)
                    s.append("K");
                else s.append(card - 38);
            }
            s.append(" ");
        }
        return s.toString();
    }

    //发牌
    private void dealCards(ArrayList<Integer> cards) {
        for (int i = 0; i < cards.size(); ++i) {
            if (i % 2 == 0)
                playerCards.add(cards.get(i));
            else
                computer1Cards.add(cards.get(i));
        }
    }

    private void handlePlayCards() {
        for (Integer card : currentPlayCards)
            playerCards.remove(card);
        currentPlayer = (currentPlayer + 1) % 2;

        //更新UI，设置按钮使能
        playButton.setEnabled(false);
        passButton.setEnabled(false);
        whoPlay.setText(new StringBuilder("你出的牌为：").append(currentPlayCardsToString()).toString());
        whoseTurn.setText("目前轮到机器人出牌");
        computer1CardsText.setText(new StringBuilder("机器人剩余").append(computer1Cards.size()).append("张牌").toString());
        for (int i = 0; i < 52; ++i) {
            if (playerCards.contains(i)) {
                playerCardsImage.get(i).setVisibility(View.VISIBLE);
                playerCardsImage.get(i).setEnabled(false);
            }
            else playerCardsImage.get(i).setVisibility(View.GONE);
        }


        currentPlayCards = new ArrayList<>();

        computerPlay();
    }

    private void handlePass() {
        currentPlayer = (currentPlayer + 1) % 2;
        currentPlayCards = new ArrayList<>();

        //更新UI，设置按钮使能
        playButton.setEnabled(false);
        passButton.setEnabled(false);
        whoPlay.setText("你未出牌");
        whoseTurn.setText("目前轮到机器人出牌");
        computer1CardsText.setText(new StringBuilder("机器人剩余").append(computer1Cards.size()).append("张牌").toString());
        for (int i = 0; i < 52; ++i) {
            if (playerCards.contains(i)) {
                playerCardsImage.get(i).setVisibility(View.VISIBLE);
                playerCardsImage.get(i).setEnabled(false);
            }
            else playerCardsImage.get(i).setVisibility(View.GONE);
        }

        computerPlay();
    }

    private void handleCardClicked(int i) {
        currentPlayCards.add(i);
    }

    private void computerPlay() {
        //通过后端AI算法得到机器人出的卡牌
        //currentPlayCards = AI_algorithm();

        if (!currentPlayCards.isEmpty())
            for (Integer card : currentPlayCards)
                computer1Cards.remove(card);

        //更新UI，设置按钮使能
        playButton.setEnabled(true);
        passButton.setEnabled(true);
        if (currentPlayCards.isEmpty())
            whoPlay.setText(new StringBuilder("机器人出的牌为：").append(currentPlayCardsToString()).toString());
        else whoPlay.setText("机器人未出牌");
        whoseTurn.setText("目前轮到你出牌");
        computer1CardsText.setText(new StringBuilder("机器人剩余").append(computer1Cards.size()).append("张牌").toString());
        for (int i = 0; i < 52; ++i) {
            if (playerCards.contains(i)) {
                playerCardsImage.get(i).setVisibility(View.VISIBLE);
                playerCardsImage.get(i).setEnabled(true);
            }
            else playerCardsImage.get(i).setVisibility(View.GONE);
        }

        currentPlayCards = new ArrayList<>();
    }
}