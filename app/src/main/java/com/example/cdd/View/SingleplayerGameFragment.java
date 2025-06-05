package com.example.cdd.View;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cdd.Controller.GameController;
import com.example.cdd.Model.Card;
import com.example.cdd.Pojo.PlayerInformation;
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
    public static final int PLAYER = 0;
    public int rule;
    public static final int SOUTH = 0, NORTH = 1;
    public int difficulty;
    public static final int EASY = 1, MEDIUM = 2, DIFFICULT = 3;

    private int cnt_click_card;

    private int[] click_num = new int[52];

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
            new AlertDialog.Builder(context)
                    .setTitle("退出游戏")
                    .setMessage("提前退出是有惩罚的哦，确定退出吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            controller.quitgame();

                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        });
        for (int i = 0; i < 52; ++i) {
            final int tmp = i;
            playerCardsImage.get(i).setOnClickListener(v -> {
                ++cnt_click_card;
                ++click_num[tmp];

                if (cnt_click_card == 1) {
                    playButton.setEnabled(true);
                    for (int j = 0; j < 52; ++j) {
                        playImage.get(j).setVisibility(View.GONE);
                        playImage2.get(j).setVisibility(View.GONE);
                    }
                    firstPlay.setText("我");
                    firstPlay.setVisibility(View.VISIBLE);
                    secondPlay.setVisibility(View.GONE);
                }

                if (click_num[tmp] % 2 == 1) {
                    currentPlayCards.add(tmp);
                    playImage.get(tmp).setVisibility(View.VISIBLE);
                }
                else {
                    currentPlayCards.remove((Object)tmp);
                    playImage.get(tmp).setVisibility(View.GONE);
                }

                if (currentPlayCards.isEmpty())
                    playButton.setEnabled(false);
                else playButton.setEnabled(true);

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
        controller = new GameController(requireContext());

        playerCards = new ArrayList<>();
        computer1Cards = new ArrayList<>();
        computer2Cards = new ArrayList<>();
        computer3Cards = new ArrayList<>();
        currentPlayCards = new ArrayList<>();

        cnt_click_card = 0;

        for (int i = 0; i < 52; ++i)
            click_num[i] = 0;

        //传入游戏规则和难度
        List<List<Card>> allocateCards = controller.initialize(rule, difficulty);

        List<Card> _playerCards = allocateCards.get(0);
        List<Card> _computer1Cards = allocateCards.get(1);
        List<Card> _computer2Cards = allocateCards.get(2);
        List<Card> _computer3Cards = allocateCards.get(3);
        for (Card card : _playerCards)
            playerCards.add(cardToInteger(card));
        for (Card card : _computer1Cards)
            computer1Cards.add(cardToInteger(card));
        for (Card card : _computer2Cards)
            computer2Cards.add(cardToInteger(card));
        for (Card card : _computer3Cards)
            computer3Cards.add(cardToInteger(card));

        //初始化UI界面
        firstPlay.setVisibility(View.GONE);
        secondPlay.setVisibility(View.GONE);
        thirdPlay.setVisibility(View.GONE);
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

        currentPlayer = controller.getCurrentPlayerIndex();

        if (currentPlayer == 0) {
            Toast.makeText(context, "你手中有♦3，请你先出牌！", Toast.LENGTH_SHORT).show();
            passButton.setEnabled(false);
        }
        else if (currentPlayer == 1) {
            Toast.makeText(context, "机器人1手中有♦3，机器人1先出牌！", Toast.LENGTH_SHORT).show();
            passButton.setEnabled(false);
            for (int i = 0; i < 52; ++i)
                playerCardsImage.get(i).setEnabled(false);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    computerPlay();
                }
            }, 0);
        }
        else if (currentPlayer == 2) {
            Toast.makeText(context, "机器人2手中有♦3，机器人2先出牌！", Toast.LENGTH_SHORT).show();
            passButton.setEnabled(false);
            for (int i = 0; i < 52; ++i)
                playerCardsImage.get(i).setEnabled(false);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    computer2Play();
                }
            }, 0);
        }
        else {
            Toast.makeText(context, "机器人3手中有♦3，机器人3先出牌！", Toast.LENGTH_SHORT).show();
            passButton.setEnabled(false);
            for (int i = 0; i < 52; ++i)
                playerCardsImage.get(i).setEnabled(false);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    computer3Play();
                }
            }, 0);
        }
    }

    protected void initData2() {
        playerCards = new ArrayList<>();
        computer1Cards = new ArrayList<>();
        computer2Cards = new ArrayList<>();
        computer3Cards = new ArrayList<>();
        currentPlayCards = new ArrayList<>();

        cnt_click_card = 0;

        for (int i = 0; i < 52; ++i)
            click_num[i] = 0;

        List<List<Card>> allocateCards = controller.selectNextRound();

        List<Card> _playerCards = allocateCards.get(0);
        List<Card> _computer1Cards = allocateCards.get(1);
        List<Card> _computer2Cards = allocateCards.get(2);
        List<Card> _computer3Cards = allocateCards.get(3);
        for (Card card : _playerCards)
            playerCards.add(cardToInteger(card));
        for (Card card : _computer1Cards)
            computer1Cards.add(cardToInteger(card));
        for (Card card : _computer2Cards)
            computer2Cards.add(cardToInteger(card));
        for (Card card : _computer3Cards)
            computer3Cards.add(cardToInteger(card));

        currentPlayer = controller.getCurrentPlayerIndex();

        //初始化UI界面
        secondPlay.setVisibility(View.GONE);
        thirdPlay.setVisibility(View.GONE);
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

        if (currentPlayer == 0) {
            Toast.makeText(context, "你手中有♦3，请你先出牌！", Toast.LENGTH_SHORT).show();
            passButton.setEnabled(false);
        }
        else if (currentPlayer == 1) {
            Toast.makeText(context, "机器人1手中有♦3，机器人1先出牌！", Toast.LENGTH_SHORT).show();
            passButton.setEnabled(false);
            for (int i = 0; i < 52; ++i)
                playerCardsImage.get(i).setEnabled(false);
            computerPlay();
        }
        else if (currentPlayer == 2) {
            Toast.makeText(context, "机器人2手中有♦3，机器人2先出牌！", Toast.LENGTH_SHORT).show();
            passButton.setEnabled(false);
            for (int i = 0; i < 52; ++i)
                playerCardsImage.get(i).setEnabled(false);
            computer2Play();
        }
        else {
            Toast.makeText(context, "机器人3手中有♦3，机器人3先出牌！", Toast.LENGTH_SHORT).show();
            passButton.setEnabled(false);
            for (int i = 0; i < 52; ++i)
                playerCardsImage.get(i).setEnabled(false);
            computer3Play();
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
        for (int i = 0; i < 52; ++i)
            click_num[i] = 0;
        cnt_click_card = 0;

        List<Card> cards = new ArrayList<>();
        for (Integer i : currentPlayCards)
            cards.add(integerToCard(i));

        if (!controller.playHandCard(cards)) { //出牌组合不合法
            Toast.makeText(context, "出牌组合不合法，请重新出牌", Toast.LENGTH_SHORT).show();

            for (int i = 0; i < 52; ++i)
                playImage.get(i).setVisibility(View.GONE);

            currentPlayCards = new ArrayList<>();
            playButton.setEnabled(false);

            return;
        }

        //出牌组合合法
        firstPlay.setText("机器人1");
        firstPlay.setVisibility(View.GONE);
        for (int i = 0; i < 52; ++i)
            playImage.get(i).setVisibility(View.GONE);
        thirdPlay.setVisibility(View.GONE);
        for (int j = 0; j < 52; ++j)
            playImage3.get(j).setVisibility(View.GONE);

        for (Integer card : currentPlayCards)
            playerCards.remove(card);

        currentPlayer = (currentPlayer + 1) % 4;

        //更新UI，设置按钮使能
        playButton.setEnabled(false);
        passButton.setEnabled(false);
        computer1CardsText.setText(new StringBuilder("机器人1剩余").append(computer1Cards.size()).append("张牌").toString());
        computer2CardsText.setText(new StringBuilder("机器人2剩余").append(computer2Cards.size()).append("张牌").toString());
        computer3CardsText.setText(new StringBuilder("机器人3剩余").append(computer3Cards.size()).append("张牌").toString());
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
                            for (int i = 0; i < 52; ++i) {
                                playImage.get(i).setVisibility(View.GONE);
                                playImage2.get(i).setVisibility(View.GONE);
                                playImage3.get(i).setVisibility(View.GONE);
                            }

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    initData2();
                                }
                            }, 0);
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
            return;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                computerPlay();
            }
        }, 0);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                computerPlay();
//            }
//        });
    }

    private void handlePass() {
        for (int i = 0; i < 52; ++i)
            click_num[i] = 0;
        cnt_click_card = 0;

        firstPlay.setVisibility(View.GONE);
        secondPlay.setVisibility(View.GONE);
        thirdPlay.setVisibility(View.GONE);

        controller.pass();

        for (int i = 0; i < 52; ++i) {
            playImage.get(i).setVisibility(View.GONE);
            playImage2.get(i).setVisibility(View.GONE);
            playImage3.get(i).setVisibility(View.GONE);
        }

        currentPlayer = (currentPlayer + 1) % 4;
        currentPlayCards = new ArrayList<>();

        //更新UI，设置按钮使能
        firstPlay.setText("机器人1");
        playButton.setEnabled(false);
        passButton.setEnabled(false);
        computer1CardsText.setText(new StringBuilder("机器人1剩余").append(computer1Cards.size()).append("张牌").toString());
        computer2CardsText.setText(new StringBuilder("机器人2剩余").append(computer2Cards.size()).append("张牌").toString());
        computer3CardsText.setText(new StringBuilder("机器人3剩余").append(computer3Cards.size()).append("张牌").toString());
        for (int i = 0; i < 52; ++i) {
            if (playerCards.contains(i)) {
                playerCardsImage.get(i).setVisibility(View.VISIBLE);
                playerCardsImage.get(i).setEnabled(false);
            }
            else playerCardsImage.get(i).setVisibility(View.GONE);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                computerPlay();
            }
        }, 0);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                computerPlay();
//            }
//        });
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
        // 禁用出牌和过牌按钮，直到电脑玩家回合结束
        playButton.setEnabled(false);
        passButton.setEnabled(false);

        // 机器人1在后台线程中出牌
        new ComputerPlayTask(1).execute();
    }

    private class ComputerPlayTask extends AsyncTask<Void, Void, List<Integer>> {
        private int robotNumber;
        private boolean hasPlayedCards = false; // 标记机器人是否出牌
        private boolean isWinner = false; // 标记机器人是否获胜

        public ComputerPlayTask(int robotNumber) {
            this.robotNumber = robotNumber;
        }

        @Override
        protected List<Integer> doInBackground(Void... voids) {
            // 如果需要，可以在此处模拟网络/计算延迟（用于测试）
            // try {
            //     Thread.sleep(500);
            // } catch (InterruptedException e) {
            //     e.printStackTrace();
            // }

            List<Card> cards = controller.robotPlayCard();
            List<Integer> playedCardIntegers = new ArrayList<>();
            for (Card card : cards) {
                playedCardIntegers.add(cardToInteger(card));
            }
            return playedCardIntegers;
        }

        @Override
        protected void onPostExecute(List<Integer> playedCards) {
            TextView robotPlayTextView;
            List<Integer> robotHand;
            List<ImageView> robotPlayImages;
            TextView robotCardsRemainingText;

            switch (robotNumber) {
                case 1:
                    robotPlayTextView = firstPlay;
                    robotHand = computer1Cards;
                    robotPlayImages = playImage;
                    robotCardsRemainingText = computer1CardsText;
                    break;
                case 2:
                    robotPlayTextView = secondPlay;
                    robotHand = computer2Cards;
                    robotPlayImages = playImage2;
                    robotCardsRemainingText = computer2CardsText;
                    break;
                case 3:
                    robotPlayTextView = thirdPlay;
                    robotHand = computer3Cards;
                    robotPlayImages = playImage3;
                    robotCardsRemainingText = computer3CardsText;
                    break;
                default:
                    return; // 不应该发生
            }

            robotPlayTextView.setVisibility(View.VISIBLE);
            if (!playedCards.isEmpty()) {
                hasPlayedCards = true;
                robotPlayTextView.setText("机器人" + robotNumber);
                for (Integer card : playedCards) {
                    robotHand.remove(card);
                    robotPlayImages.get(card).setVisibility(View.VISIBLE);
                }
                if (robotHand.isEmpty()) {
                    isWinner = true;
                    showEndGameDialog("机器人" + robotNumber + "最先出完牌，赢得游戏！");
                    return;
                }
            } else {
                robotPlayTextView.setText("机器人" + robotNumber + "\n过牌");
            }
            robotCardsRemainingText.setText(new StringBuilder("机器人").append(robotNumber).append("剩余").append(robotHand.size()).append("张牌").toString());

            // 继续下一个机器人的回合或玩家的回合
            if (!isWinner) {
                if (robotNumber < 3) {
                    // 执行下一个机器人的出牌任务
                    new ComputerPlayTask(robotNumber + 1).execute();
                } else {
                    // 所有机器人已出牌，现在轮到玩家
                    currentPlayer = PLAYER;

                    // 更新玩家回合的UI
                    // playButton.setEnabled(false); // 此项已在开始时设置，如果需要，为玩家重新启用
                    // passButton.setEnabled(true); // 如果任何机器人出牌，重新启用过牌按钮

                    // 检查是否有机器人出牌以启用过牌按钮
                    // 这里的逻辑需要根据您的游戏规则进行调整，如果所有机器人都过牌，那么玩家可以重新开始出牌
                    if (firstPlay.getText().toString().contains("过牌") &&
                            secondPlay.getText().toString().contains("过牌") &&
                            thirdPlay.getText().toString().contains("过牌")) {
                        passButton.setEnabled(false); // 所有机器人过牌，玩家不能过牌
                    } else {
                        passButton.setEnabled(true); // 有机器人出牌，玩家可以选择过牌
                    }
                    playButton.setEnabled(true); // 玩家可以尝试出牌

                    for (int i = 0; i < 52; ++i) {
                        if (playerCards.contains(i)) {
                            playerCardsImage.get(i).setVisibility(View.VISIBLE);
                            playerCardsImage.get(i).setEnabled(true);
                        } else {
                            playerCardsImage.get(i).setVisibility(View.GONE);
                        }
                    }
                    // 重置 currentPlayCards 以供玩家回合使用（或根据规则保留最后出的牌）
                    // 如果 currentPlayCards 用于表示玩家需要压制的牌，那么它应该由最后一个出牌的机器人更新。
                    // 在此示例中，如果所有机器人都过牌，则清空它，否则它应包含最后出的牌。
                    if (!playedCards.isEmpty()) { // 这是针对当前机器人（此处为机器人3）
                        currentPlayCards.clear();
                        currentPlayCards.addAll(playedCards);
                    } else {
                        // 如果机器人3过牌，则检查机器人2，然后机器人1。这需要更复杂的逻辑来追踪。
                        // 暂时，如果所有机器人过牌，假设 currentPlayCards 应该为空，以便玩家开始新一轮。
                        if (!firstPlay.getText().toString().contains("过牌") ||
                                !secondPlay.getText().toString().contains("过牌") ||
                                !thirdPlay.getText().toString().contains("过牌")) {
                            // 有机器人出牌，因此 currentPlayCards 应该保留最后有效的出牌
                            // 此逻辑需要从外部范围访问 currentPlayCards2 和 currentPlayCards3
                            // 或者更复杂的状态管理。
                            // 为了简化此示例，假设 currentPlayCards 将正确反映最后一次出牌。
                        } else {
                            currentPlayCards.clear(); // 所有机器人过牌，玩家重新开始
                        }
                    }
                }
            }
        }
    }

    // 显示游戏结束对话框的辅助方法
    private void showEndGameDialog(String message) {
        new AlertDialog.Builder(context)
                .setTitle("游戏结束")
                .setMessage(message)
                .setPositiveButton("再来一轮", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 重置UI以开始新一轮
                        for (int i = 0; i < 52; ++i) {
                            playImage.get(i).setVisibility(View.GONE);
                            playImage2.get(i).setVisibility(View.GONE);
                            playImage3.get(i).setVisibility(View.GONE);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                initData2(); // 重新初始化游戏数据
                            }
                        }, 0);
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
                .setCancelable(false) // 禁止通过点击外部或返回键取消对话框
                .show();
    }

    private void computer2Play() {
        // 确保机器人1的牌数已更新
        computer1CardsText.setText(new StringBuilder("机器人1剩余").append(computer1Cards.size()).append("张牌").toString());

        // 禁用出牌和过牌按钮，直到电脑玩家回合结束
        playButton.setEnabled(false);
        passButton.setEnabled(false);

        // 从机器人2开始执行出牌任务
        new ComputerPlayTask(2).execute(); // 注意：从机器人2开始
    }


    // 显示游戏结束对话框的辅助方法 (与之前版本相同，为了完整性再次列出)

    private void computer3Play() {
        // 确保机器人1和机器人2的牌数已更新
        computer1CardsText.setText(new StringBuilder("机器人1剩余").append(computer1Cards.size()).append("张牌").toString());
        computer2CardsText.setText(new StringBuilder("机器人2剩余").append(computer2Cards.size()).append("张牌").toString());

        // 禁用出牌和过牌按钮，直到电脑玩家回合结束
        playButton.setEnabled(false);
        passButton.setEnabled(false);

        // 从机器人3开始执行出牌任务
        new ComputerPlayTask(3).execute(); // 注意：从机器人3开始
    }

    public void receiveMessage(String message) {
        if (message.equals("south")) {
            rule = SOUTH;
        }
        else if (message.equals("north")) {
            rule = NORTH;
        }
        else if (message.equals("easy")) {
            difficulty = EASY;
        }
        else if (message.equals("medium")) {
            difficulty = MEDIUM;
        }
        else if (message.equals("difficult")) {
            difficulty = DIFFICULT;
        }
    }
}