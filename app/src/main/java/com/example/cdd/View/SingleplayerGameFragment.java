package com.example.cdd.View;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cdd.Controller.GameController;
import com.example.cdd.Model.Card;
import com.example.cdd.R;

import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

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
    private GameController controller;
    private ArrayList<ImageView> playImage;
    private ArrayList<ImageView> playImage2;
    private ArrayList<ImageView> playImage3;
    private ArrayList<Button> playerCardsImage; //显示玩家手中的扑克牌
    private TextView firstPlay, secondPlay, thirdPlay;
    private TextView whoseTurn; //轮到谁出牌
    private TextView computer1CardsText; //显示机器人1剩余的牌数
    private TextView computer2CardsText; //显示机器人2剩余的牌数
    private TextView computer3CardsText; //显示机器人3剩余的牌数
    private Button playButton; //出牌
    private Button passButton; //过牌
    private Button quitButton; //游戏中途退出

    // 游戏数据
    private ArrayList<Integer> playerCards;
    private ArrayList<Integer> computer1Cards;
    private ArrayList<Integer> computer2Cards;
    private ArrayList<Integer> computer3Cards;
    private ArrayList<Integer> currentPlayCards;
    private int currentPlayer;
    public static final int PLAYER = 0, COMPUTER1 = 1;

    private int cnt_click_card;

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
        //初始化出牌图片控件
        playImage = new ArrayList<>();
        playImage.add(view.findViewById(R.id.i_cluba));
        playImage.add(view.findViewById(R.id.i_club2));
        playImage.add(view.findViewById(R.id.i_club3));
        playImage.add(view.findViewById(R.id.i_club4));
        playImage.add(view.findViewById(R.id.i_club5));
        playImage.add(view.findViewById(R.id.i_club6));
        playImage.add(view.findViewById(R.id.i_club7));
        playImage.add(view.findViewById(R.id.i_club8));
        playImage.add(view.findViewById(R.id.i_club9));
        playImage.add(view.findViewById(R.id.i_club10));
        playImage.add(view.findViewById(R.id.i_clubj));
        playImage.add(view.findViewById(R.id.i_clubq));
        playImage.add(view.findViewById(R.id.i_clubk));

        playImage.add(view.findViewById(R.id.i_hearta));
        playImage.add(view.findViewById(R.id.i_heart2));
        playImage.add(view.findViewById(R.id.i_heart3));
        playImage.add(view.findViewById(R.id.i_heart4));
        playImage.add(view.findViewById(R.id.i_heart5));
        playImage.add(view.findViewById(R.id.i_heart6));
        playImage.add(view.findViewById(R.id.i_heart7));
        playImage.add(view.findViewById(R.id.i_heart8));
        playImage.add(view.findViewById(R.id.i_heart9));
        playImage.add(view.findViewById(R.id.i_heart10));
        playImage.add(view.findViewById(R.id.i_heartj));
        playImage.add(view.findViewById(R.id.i_heartq));
        playImage.add(view.findViewById(R.id.i_heartk));

        playImage.add(view.findViewById(R.id.i_diamonda));
        playImage.add(view.findViewById(R.id.i_diamond2));
        playImage.add(view.findViewById(R.id.i_diamond3));
        playImage.add(view.findViewById(R.id.i_diamond4));
        playImage.add(view.findViewById(R.id.i_diamond5));
        playImage.add(view.findViewById(R.id.i_diamond6));
        playImage.add(view.findViewById(R.id.i_diamond7));
        playImage.add(view.findViewById(R.id.i_diamond8));
        playImage.add(view.findViewById(R.id.i_diamond9));
        playImage.add(view.findViewById(R.id.i_diamond10));
        playImage.add(view.findViewById(R.id.i_diamondj));
        playImage.add(view.findViewById(R.id.i_diamondq));
        playImage.add(view.findViewById(R.id.i_diamondk));

        playImage.add(view.findViewById(R.id.i_spadea));
        playImage.add(view.findViewById(R.id.i_spade2));
        playImage.add(view.findViewById(R.id.i_spade3));
        playImage.add(view.findViewById(R.id.i_spade4));
        playImage.add(view.findViewById(R.id.i_spade5));
        playImage.add(view.findViewById(R.id.i_spade6));
        playImage.add(view.findViewById(R.id.i_spade7));
        playImage.add(view.findViewById(R.id.i_spade8));
        playImage.add(view.findViewById(R.id.i_spade9));
        playImage.add(view.findViewById(R.id.i_spade10));
        playImage.add(view.findViewById(R.id.i_spadej));
        playImage.add(view.findViewById(R.id.i_spadeq));
        playImage.add(view.findViewById(R.id.i_spadek));

        playImage2 = new ArrayList<>();
        playImage2.add(view.findViewById(R.id.i2_cluba));
        playImage2.add(view.findViewById(R.id.i2_club2));
        playImage2.add(view.findViewById(R.id.i2_club3));
        playImage2.add(view.findViewById(R.id.i2_club4));
        playImage2.add(view.findViewById(R.id.i2_club5));
        playImage2.add(view.findViewById(R.id.i2_club6));
        playImage2.add(view.findViewById(R.id.i2_club7));
        playImage2.add(view.findViewById(R.id.i2_club8));
        playImage2.add(view.findViewById(R.id.i2_club9));
        playImage2.add(view.findViewById(R.id.i2_club10));
        playImage2.add(view.findViewById(R.id.i2_clubj));
        playImage2.add(view.findViewById(R.id.i2_clubq));
        playImage2.add(view.findViewById(R.id.i2_clubk));

        playImage2.add(view.findViewById(R.id.i2_hearta));
        playImage2.add(view.findViewById(R.id.i2_heart2));
        playImage2.add(view.findViewById(R.id.i2_heart3));
        playImage2.add(view.findViewById(R.id.i2_heart4));
        playImage2.add(view.findViewById(R.id.i2_heart5));
        playImage2.add(view.findViewById(R.id.i2_heart6));
        playImage2.add(view.findViewById(R.id.i2_heart7));
        playImage2.add(view.findViewById(R.id.i2_heart8));
        playImage2.add(view.findViewById(R.id.i2_heart9));
        playImage2.add(view.findViewById(R.id.i2_heart10));
        playImage2.add(view.findViewById(R.id.i2_heartj));
        playImage2.add(view.findViewById(R.id.i2_heartq));
        playImage2.add(view.findViewById(R.id.i2_heartk));

        playImage2.add(view.findViewById(R.id.i2_diamonda));
        playImage2.add(view.findViewById(R.id.i2_diamond2));
        playImage2.add(view.findViewById(R.id.i2_diamond3));
        playImage2.add(view.findViewById(R.id.i2_diamond4));
        playImage2.add(view.findViewById(R.id.i2_diamond5));
        playImage2.add(view.findViewById(R.id.i2_diamond6));
        playImage2.add(view.findViewById(R.id.i2_diamond7));
        playImage2.add(view.findViewById(R.id.i2_diamond8));
        playImage2.add(view.findViewById(R.id.i2_diamond9));
        playImage2.add(view.findViewById(R.id.i2_diamond10));
        playImage2.add(view.findViewById(R.id.i2_diamondj));
        playImage2.add(view.findViewById(R.id.i2_diamondq));
        playImage2.add(view.findViewById(R.id.i2_diamondk));

        playImage2.add(view.findViewById(R.id.i2_spadea));
        playImage2.add(view.findViewById(R.id.i2_spade2));
        playImage2.add(view.findViewById(R.id.i2_spade3));
        playImage2.add(view.findViewById(R.id.i2_spade4));
        playImage2.add(view.findViewById(R.id.i2_spade5));
        playImage2.add(view.findViewById(R.id.i2_spade6));
        playImage2.add(view.findViewById(R.id.i2_spade7));
        playImage2.add(view.findViewById(R.id.i2_spade8));
        playImage2.add(view.findViewById(R.id.i2_spade9));
        playImage2.add(view.findViewById(R.id.i2_spade10));
        playImage2.add(view.findViewById(R.id.i2_spadej));
        playImage2.add(view.findViewById(R.id.i2_spadeq));
        playImage2.add(view.findViewById(R.id.i2_spadek));

        playImage3 = new ArrayList<>();

        playImage3.add(view.findViewById(R.id.i3_cluba));
        playImage3.add(view.findViewById(R.id.i3_club2));
        playImage3.add(view.findViewById(R.id.i3_club3));
        playImage3.add(view.findViewById(R.id.i3_club4));
        playImage3.add(view.findViewById(R.id.i3_club5));
        playImage3.add(view.findViewById(R.id.i3_club6));
        playImage3.add(view.findViewById(R.id.i3_club7));
        playImage3.add(view.findViewById(R.id.i3_club8));
        playImage3.add(view.findViewById(R.id.i3_club9));
        playImage3.add(view.findViewById(R.id.i3_club10));
        playImage3.add(view.findViewById(R.id.i3_clubj));
        playImage3.add(view.findViewById(R.id.i3_clubq));
        playImage3.add(view.findViewById(R.id.i3_clubk));

        playImage3.add(view.findViewById(R.id.i3_hearta));
        playImage3.add(view.findViewById(R.id.i3_heart2));
        playImage3.add(view.findViewById(R.id.i3_heart3));
        playImage3.add(view.findViewById(R.id.i3_heart4));
        playImage3.add(view.findViewById(R.id.i3_heart5));
        playImage3.add(view.findViewById(R.id.i3_heart6));
        playImage3.add(view.findViewById(R.id.i3_heart7));
        playImage3.add(view.findViewById(R.id.i3_heart8));
        playImage3.add(view.findViewById(R.id.i3_heart9));
        playImage3.add(view.findViewById(R.id.i3_heart10));
        playImage3.add(view.findViewById(R.id.i3_heartj));
        playImage3.add(view.findViewById(R.id.i3_heartq));
        playImage3.add(view.findViewById(R.id.i3_heartk));

        playImage3.add(view.findViewById(R.id.i3_diamonda));
        playImage3.add(view.findViewById(R.id.i3_diamond2));
        playImage3.add(view.findViewById(R.id.i3_diamond3));
        playImage3.add(view.findViewById(R.id.i3_diamond4));
        playImage3.add(view.findViewById(R.id.i3_diamond5));
        playImage3.add(view.findViewById(R.id.i3_diamond6));
        playImage3.add(view.findViewById(R.id.i3_diamond7));
        playImage3.add(view.findViewById(R.id.i3_diamond8));
        playImage3.add(view.findViewById(R.id.i3_diamond9));
        playImage3.add(view.findViewById(R.id.i3_diamond10));
        playImage3.add(view.findViewById(R.id.i3_diamondj));
        playImage3.add(view.findViewById(R.id.i3_diamondq));
        playImage3.add(view.findViewById(R.id.i3_diamondk));

        playImage3.add(view.findViewById(R.id.i3_spadea));
        playImage3.add(view.findViewById(R.id.i3_spade2));
        playImage3.add(view.findViewById(R.id.i3_spade3));
        playImage3.add(view.findViewById(R.id.i3_spade4));
        playImage3.add(view.findViewById(R.id.i3_spade5));
        playImage3.add(view.findViewById(R.id.i3_spade6));
        playImage3.add(view.findViewById(R.id.i3_spade7));
        playImage3.add(view.findViewById(R.id.i3_spade8));
        playImage3.add(view.findViewById(R.id.i3_spade9));
        playImage3.add(view.findViewById(R.id.i3_spade10));
        playImage3.add(view.findViewById(R.id.i3_spadej));
        playImage3.add(view.findViewById(R.id.i3_spadeq));
        playImage3.add(view.findViewById(R.id.i3_spadek));

        for (int i = 0; i < 52; ++i) {
            playImage.get(i).setVisibility(View.GONE);
            playImage2.get(i).setVisibility(View.GONE);
            playImage3.get(i).setVisibility(View.GONE);
        }

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
        computer2CardsText = view.findViewById(R.id.computer2_cards_text);
        computer3CardsText = view.findViewById(R.id.computer3_cards_text);

        //初始化出牌者
        firstPlay = view.findViewById(R.id.first_play);
        secondPlay = view.findViewById(R.id.second_play);
        thirdPlay = view.findViewById(R.id.third_play);

        //初始化该轮到谁
        whoseTurn = view.findViewById(R.id.whose_turn);
        whoseTurn.setVisibility(View.GONE);

        // 初始化操作按钮
        playButton = view.findViewById(R.id.play_button);
        passButton = view.findViewById(R.id.pass_button);
        quitButton = view.findViewById(R.id.quit_button);

        // 设置按钮点击事件
        playButton.setOnClickListener(v -> handlePlayCards());
        passButton.setOnClickListener(v -> handlePass());
        quitButton.setOnClickListener(v -> {
            controller.quitgame();

            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        for (int i = 0; i < 52; ++i) {
            final int tmp = i;
            playerCardsImage.get(i).setOnClickListener(v -> {
                ++cnt_click_card;

                if (cnt_click_card == 1) {
                    playButton.setEnabled(true);
                    for (int j = 0; j < 52; ++j) {
                        playImage.get(j).setVisibility(View.GONE);
                        playImage2.get(j).setVisibility(View.GONE);
                        playImage3.get(j).setVisibility(View.GONE);
                    }
                    firstPlay.setVisibility(View.GONE);
                    firstPlay.setText("机器人1");
                    secondPlay.setVisibility(View.GONE);
                    thirdPlay.setVisibility(View.GONE);
                }

                currentPlayCards.add(tmp);

                playImage.get(tmp).setVisibility(View.VISIBLE);

                // 上移动画 - 向上移动50像素
                ObjectAnimator moveUp = ObjectAnimator.ofFloat(v, "translationY", -50f);
                moveUp.setDuration(200); // 动画持续时间200毫秒

                // 下移动画 - 回到原位
                ObjectAnimator moveDown = ObjectAnimator.ofFloat(v, "translationY", 0f);
                moveDown.setDuration(200);

                // 按顺序执行动画
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playSequentially(moveUp, moveDown);
                animatorSet.start();
            });
        }
    }

    @Override
    protected void initData(Context context) {
        controller = new GameController();

        playerCards = new ArrayList<>();
        computer1Cards = new ArrayList<>();
        computer2Cards = new ArrayList<>();
        computer3Cards = new ArrayList<>();
        currentPlayCards = new ArrayList<>();

        currentPlayer = 0;

        cnt_click_card = 0;

        List<List<Card>> allocateCards = new ArrayList<>();
        allocateCards.add(new ArrayList<>());
        allocateCards.add(new ArrayList<>());
        allocateCards.add(new ArrayList<>());
        allocateCards.add(new ArrayList<>());
        //allocateCards = controller.initialize(传入参数);

        ArrayList<Card> _playerCards = (ArrayList<Card>) allocateCards.get(0);
        ArrayList<Card> _computer1Cards = (ArrayList<Card>) allocateCards.get(1);
        ArrayList<Card> _computer2Cards = (ArrayList<Card>) allocateCards.get(2);
        ArrayList<Card> _computer3Cards = (ArrayList<Card>) allocateCards.get(3);
        for (Card card : _playerCards)
            playerCards.add(cardToInteger(card));
        for (Card card : _computer1Cards)
            computer1Cards.add(cardToInteger(card));
        for (Card card : _computer2Cards)
            computer2Cards.add(cardToInteger(card));
        for (Card card : _computer3Cards)
            computer3Cards.add(cardToInteger(card));

        //初始化UI界面
        firstPlay.setText("等待你出牌");
        secondPlay.setVisibility(View.GONE);
        thirdPlay.setVisibility(View.GONE);
        whoseTurn.setText("目前轮到你出牌");
        computer1CardsText.setText("机器人1剩余13张牌");
        computer2CardsText.setText("机器人2剩余13张牌");
        computer3CardsText.setText("机器人3剩余13张牌");
        playButton.setEnabled(false);
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

    //发牌
    private void dealCards(ArrayList<Integer> cards) {
        for (int i = 0; i < cards.size(); ++i) {
            if (i % 4 == 0)
                playerCards.add(cards.get(i));
            else if (i % 4 == 1)
                computer1Cards.add(cards.get(i));
            else if (i % 4 == 2)
                computer2Cards.add(cards.get(i));
            else computer3Cards.add(cards.get(i));
        }
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

    private void handlePlayCards() {

        ArrayList<Card> cards = new ArrayList<>();
        for (int i : currentPlayCards)
            cards.add(integerToCard(i));

        if (!controller.playHandCard(cards)) {
            Toast.makeText(context, "出牌组合不合法，请重新出牌", Toast.LENGTH_SHORT).show();

            cnt_click_card = 0;

            for (int i = 0; i < 52; ++i)
                playImage.get(i).setVisibility(View.GONE);

            currentPlayCards = new ArrayList<>();

            playButton.setEnabled(false);

            return;
        }

        cnt_click_card = 0;

        for (int i = 0; i < 52; ++i)
            playImage.get(i).setVisibility(View.GONE);

        for (Integer card : currentPlayCards)
            playerCards.remove(card);
		
        currentPlayer = (currentPlayer + 1) % 4;

        //更新UI，设置按钮使能
        playButton.setEnabled(false);
        passButton.setEnabled(false);
        whoseTurn.setText("目前轮到机器人出牌");
        computer1CardsText.setText(new StringBuilder("机器人1剩余").append(computer1Cards.size()).append("张牌").toString());
        computer1CardsText.setText(new StringBuilder("机器人2剩余").append(computer2Cards.size()).append("张牌").toString());
        computer1CardsText.setText(new StringBuilder("机器人3剩余").append(computer3Cards.size()).append("张牌").toString());
        for (int i = 0; i < 52; ++i) {
            if (playerCards.contains(i)) {
                playerCardsImage.get(i).setVisibility(View.VISIBLE);
                playerCardsImage.get(i).setEnabled(false);
            }
            else playerCardsImage.get(i).setVisibility(View.GONE);
        }

        currentPlayCards = new ArrayList<>();

        if (playerCards.isEmpty()) {
            new AlertDialog.Builder(context)
                    .setTitle("游戏结束")
                    .setMessage("你最先出完牌，恭喜你赢得游戏！")
                    .setPositiveButton("再来一轮", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            controller.selectNextRound();

                            for (int i = 0; i < 52; ++i) {
                                playImage.get(i).setVisibility(View.GONE);
                                playImage2.get(i).setVisibility(View.GONE);
                                playImage3.get(i).setVisibility(View.GONE);
                            }

                            initData(context);
                        }
                    }).setNegativeButton("退出游戏", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            controller.endgame();

                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 清除Activity栈
                            startActivity(intent);
                        }
                    })
                    .show();
        }

        computerPlay();
    }

    private void handlePass() {
        controller.pass();

        cnt_click_card = 0;

        for (int i = 0; i < 52; ++i)
            playImage.get(i).setVisibility(View.GONE);

        currentPlayer = (currentPlayer + 1) % 4;
        currentPlayCards = new ArrayList<>();

        //更新UI，设置按钮使能
        playButton.setEnabled(false);
        passButton.setEnabled(false);
        whoseTurn.setText("目前轮到机器人出牌");
        computer1CardsText.setText(new StringBuilder("机器人1剩余").append(computer1Cards.size()).append("张牌").toString());
        computer1CardsText.setText(new StringBuilder("机器人2剩余").append(computer2Cards.size()).append("张牌").toString());
        computer1CardsText.setText(new StringBuilder("机器人3剩余").append(computer3Cards.size()).append("张牌").toString());
        for (int i = 0; i < 52; ++i) {
            if (playerCards.contains(i)) {
                playerCardsImage.get(i).setVisibility(View.VISIBLE);
                playerCardsImage.get(i).setEnabled(false);
            }
            else playerCardsImage.get(i).setVisibility(View.GONE);
        }

        computerPlay();
    }

    public static int cardToInteger(Card card) {
        if (card.getSuit() == Card.Suit.Club) {
            if (card.getRank() == Card.Rank.ACE)
                return 0;
            else if (card.getRank() == Card.Rank.TWO)
                return 1;
            else return card.getRank().getValue() - 1;
        }
        else if (card.getSuit() == Card.Suit.Heart) {
            if (card.getRank() == Card.Rank.ACE)
                return 13;
            else if (card.getRank() == Card.Rank.TWO)
                return 14;
            else return 13 + card.getRank().getValue() - 1;
        }
        else if (card.getSuit() == Card.Suit.Diamond) {
            if (card.getRank() == Card.Rank.ACE)
                return 26;
            else if (card.getRank() == Card.Rank.TWO)
                return 27;
            else return 26 + card.getRank().getValue() - 1;
        }
        else {
            if (card.getRank() == Card.Rank.ACE)
                return 39;
            else if (card.getRank() == Card.Rank.TWO)
                return 40;
            else return 39 + card.getRank().getValue() - 1;
        }
    }

    public static Card integerToCard(int i) {
        if (i >= 0 && i <= 12) {
            if (i >= 2) i -= 2;
            else i += 11;
            return new Card(i, 0);
        }
        else if (i >= 13 && i <= 25) {
            i -= 13;
            if (i >= 2) i -= 2;
            else i += 11;
            return new Card(i, 2);
        }
        else if (i >= 26 && i <= 38) {
            i -= 26;
            if (i >= 2) i -= 2;
            else i += 11;
            return new Card(i, 1);
        }
        else {
            i -= 39;
            if (i >= 2) i -= 2;
            else i += 11;
            return new Card(i, 3);
        }
    }

    private void computerPlay() {
        //通过后端AI算法得到机器人出的卡牌
        ArrayList<Card> cards = (ArrayList<Card>) controller.robotPlayCard();
        for (Card card : cards) {
            currentPlayCards.add(cardToInteger(card));
        }

        if (!currentPlayCards.isEmpty()) {
            for (Integer card : currentPlayCards) {
                computer1Cards.remove(card);
                playImage.get(card).setVisibility(View.VISIBLE);
            }
            if (computer1Cards.isEmpty()) {
                new AlertDialog.Builder(context)
                        .setTitle("游戏结束")
                        .setMessage("机器人1最先出完牌，赢得游戏！")
                        .setPositiveButton("再来一轮", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                controller.selectNextRound();

                                for (int i = 0; i < 52; ++i) {
                                    playImage.get(i).setVisibility(View.GONE);
                                    playImage2.get(i).setVisibility(View.GONE);
                                    playImage3.get(i).setVisibility(View.GONE);
                                }

                                initData(context);
                            }
                        }).setNegativeButton("退出游戏", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                controller.endgame();

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }

        cards = (ArrayList<Card>) controller.robotPlayCard();
        ArrayList<Integer> currentPlayCards2 = new ArrayList<>();
        for (Card card : cards) {
            currentPlayCards2.add(cardToInteger(card));
        }

        if (!currentPlayCards2.isEmpty()) {
            for (Integer card : currentPlayCards2) {
                computer2Cards.remove(card);
                playImage2.get(card).setVisibility(View.VISIBLE);
            }
            if (computer2Cards.isEmpty()) {
                new AlertDialog.Builder(context)
                        .setTitle("游戏结束")
                        .setMessage("机器人2最先出完牌，赢得游戏！")
                        .setPositiveButton("再来一轮", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                controller.selectNextRound();

                                for (int i = 0; i < 52; ++i) {
                                    playImage.get(i).setVisibility(View.GONE);
                                    playImage2.get(i).setVisibility(View.GONE);
                                    playImage3.get(i).setVisibility(View.GONE);
                                }

                                initData(context);
                            }
                        }).setNegativeButton("退出游戏", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                controller.endgame();

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 清除Activity栈
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }

        cards = (ArrayList<Card>) controller.robotPlayCard();
        ArrayList<Integer> currentPlayCards3 = new ArrayList<>();
        for (Card card : cards) {
            currentPlayCards3.add(cardToInteger(card));
        }

        if (!currentPlayCards3.isEmpty()) {
            for (Integer card : currentPlayCards3) {
                computer3Cards.remove(card);
                playImage3.get(card).setVisibility(View.VISIBLE);
            }
            if (computer3Cards.isEmpty()) {
                new AlertDialog.Builder(context)
                        .setTitle("游戏结束")
                        .setMessage("机器人3最先出完牌，赢得游戏！")
                        .setPositiveButton("再来一轮", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                controller.selectNextRound();

                                for (int i = 0; i < 52; ++i) {
                                    playImage.get(i).setVisibility(View.GONE);
                                    playImage2.get(i).setVisibility(View.GONE);
                                    playImage3.get(i).setVisibility(View.GONE);
                                }

                                initData(context);
                            }
                        }).setNegativeButton("退出游戏", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                controller.endgame();

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 清除Activity栈
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }

        firstPlay.setVisibility(View.VISIBLE);
        secondPlay.setVisibility(View.VISIBLE);
        thirdPlay.setVisibility(View.VISIBLE);
		
		currentPlayer = PLAYER;
		
        //更新UI，设置按钮使能
        playButton.setEnabled(false);
        passButton.setEnabled(true);
        whoseTurn.setText("目前轮到你出牌");
        computer1CardsText.setText(new StringBuilder("机器人1剩余").append(computer1Cards.size()).append("张牌").toString());
        computer1CardsText.setText(new StringBuilder("机器人2剩余").append(computer2Cards.size()).append("张牌").toString());
        computer1CardsText.setText(new StringBuilder("机器人3剩余").append(computer3Cards.size()).append("张牌").toString());
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
