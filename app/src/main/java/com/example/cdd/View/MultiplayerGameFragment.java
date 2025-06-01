package com.example.cdd.View;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cdd.Controller.GameController;
import com.example.cdd.R;

import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

import java.util.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MultiplayerGameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
// 多人联机游戏主界面
public class MultiplayerGameFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //游戏相关控件
	private ArrayList<ImageView> playImage;
    private ArrayList<Button> playerCardsImage;
    private TextView whoPlay;
    private TextView whoseTurn;
    private TextView player1CardsText;
    private TextView player2CardsText;
    private TextView player3CardsText;
    private Button playButton;
    private Button passButton;
	private Button startButton;

    //游戏数据
    private GameController controller;
    private ArrayList<Integer> myCards;
    private ArrayList<Integer> player1Cards;
    private ArrayList<Integer> player2Cards;
    private ArrayList<Integer> player3Cards;
    private ArrayList<Integer> currentPlayCards;
    int currentPlayer;
    public static final int ME = 0, PLAYER1 = 1, PLAYER2 = 2, PLAYER3 = 3;
	private int cnt_click_card;


    public MultiplayerGameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MultiplayerGameFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MultiplayerGameFragment newInstance(String param1, String param2) {
        MultiplayerGameFragment fragment = new MultiplayerGameFragment();
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

   // @Override
    protected int layoutId() {
        return R.layout.fragment_multiplayer_game;
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

        for (int i = 0; i < 52; ++i)
            playImage.get(i).setVisibility(View.GONE);
		
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

        //初始化其他三个人剩余牌数显示文本
        player1CardsText = view.findViewById(R.id.player1_cards_text);
        player2CardsText = view.findViewById(R.id.player2_cards_text);
        player3CardsText = view.findViewById(R.id.player3_cards_text);

        //初始化出牌者
        whoPlay = view.findViewById(R.id.who_play);

        //初始化该轮到谁
        whoseTurn = view.findViewById(R.id.whose_turn);
        whoseTurn.setVisibility(View.GONE);

        // 初始化操作按钮
        playButton = view.findViewById(R.id.play_button);
        passButton = view.findViewById(R.id.pass_button);
		startButton = view.findViewById(R.id.start_button);

        //设置按钮点击事件
        playButton.setOnClickListener(v -> handlePlayCards());
		playButton.setEnabled(false);
        passButton.setOnClickListener(v -> handlePass());
		passButton.setEnabled(false);
        for (int i = 0; i < 52; ++i) {
            final int tmp = i;
			playerCardsImage.get(i).setEnabled(false);
            playerCardsImage.get(i).setOnClickListener(v -> {
				++cnt_click_card;

                if (cnt_click_card == 1) {
                    for (int j = 0; j < 52; ++j)
                        playImage.get(j).setVisibility(View.GONE);
                    whoPlay.setVisibility(View.GONE);
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
		startButton.setEnabled(true);
		startButton.setOnClickListener(v -> startGame());
    }

    @Override
    protected void initData(Context context) {
        myCards = new ArrayList<>();
        player1Cards = new ArrayList<>();
        player2Cards = new ArrayList<>();
        player3Cards = new ArrayList<>();
        currentPlayCards = new ArrayList<>();
		currentPlayer = -1; //游戏开始后从后端程序获取
		cnt_click_card = 0;
    }
	
	void startGame() {
		startButton.setEnabled(false);
		
		//从后端程序获取第一个开始游戏的玩家是谁
        //currentPlayer = 后端();

        //从后端程序获取分配给自己和其他三个人的牌
        //myCards = 后端();
		//player1Cards = 后端();
		//player2Cards = 后端();
		//player3Cards = 后端();

        //初始化UI界面
        if (currentPlayer == ME) {
            whoPlay.setText("等待你出牌");
            whoseTurn.setText("目前轮到你出牌");
            player1CardsText.setText("玩家1剩余13张牌");
            player2CardsText.setText("玩家2剩余13张牌");
            player3CardsText.setText("玩家3剩余13张牌");
            playButton.setEnabled(true);
            passButton.setEnabled(true);

            for (int i = 0; i < 52; ++i) {
                if (myCards.contains(i)) {
                    playerCardsImage.get(i).setVisibility(View.VISIBLE);
                    playerCardsImage.get(i).setEnabled(true);
                }
                else playerCardsImage.get(i).setVisibility(View.GONE);
            }
        }
        else if (currentPlayer == PLAYER1) {
            whoPlay.setText("等待玩家1出牌");
            whoseTurn.setText("目前轮到玩家1出牌");
            player1CardsText.setText("玩家1剩余13张牌");
            player2CardsText.setText("玩家2剩余13张牌");
            player3CardsText.setText("玩家3剩余13张牌");
            playButton.setEnabled(false);
            passButton.setEnabled(false);

            for (int i = 0; i < 52; ++i) {
                if (myCards.contains(i)) {
                    playerCardsImage.get(i).setVisibility(View.VISIBLE);
                    playerCardsImage.get(i).setEnabled(false);
                }
                else playerCardsImage.get(i).setVisibility(View.GONE);
            }

            player1Play();
        }
        else if (currentPlayer == PLAYER2) {
            whoPlay.setText("等待玩家2出牌");
            whoseTurn.setText("目前轮到玩家2出牌");
            player1CardsText.setText("玩家1剩余13张牌");
            player2CardsText.setText("玩家2剩余13张牌");
            player3CardsText.setText("玩家3剩余13张牌");
            playButton.setEnabled(false);
            passButton.setEnabled(false);

            for (int i = 0; i < 52; ++i) {
                if (myCards.contains(i)) {
                    playerCardsImage.get(i).setVisibility(View.VISIBLE);
                    playerCardsImage.get(i).setEnabled(false);
                }
                else playerCardsImage.get(i).setVisibility(View.GONE);
            }

            player2Play();
        }
        else {
            whoPlay.setText("等待玩家3出牌");
            whoseTurn.setText("目前轮到玩家3出牌");
            player1CardsText.setText("玩家1剩余13张牌");
            player2CardsText.setText("玩家2剩余13张牌");
            player3CardsText.setText("玩家3剩余13张牌");
            playButton.setEnabled(false);
            passButton.setEnabled(false);

            for (int i = 0; i < 52; ++i) {
                if (myCards.contains(i)) {
                    playerCardsImage.get(i).setVisibility(View.VISIBLE);
                    playerCardsImage.get(i).setEnabled(false);
                }
                else playerCardsImage.get(i).setVisibility(View.GONE);
            }

            player3Play();
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
		cnt_click_card = 0;
		
		for (int i = 0; i < 52; ++i) {
            playImage.get(i).setVisibility(View.GONE);
        }
		
        for (Integer card : currentPlayCards) {
            myCards.remove(card);
        }
		
        currentPlayer = (currentPlayer + 1) % 4;

        //更新UI，设置按钮使能
        playButton.setEnabled(false);
        passButton.setEnabled(false);
        whoPlay.setText(new StringBuilder("你出的牌为：").append(currentPlayCardsToString()).toString());
        whoseTurn.setText("目前轮到玩家1出牌");
        player1CardsText.setText(new StringBuilder("玩家1剩余").append(player1Cards.size()).append("张牌").toString());
        player2CardsText.setText(new StringBuilder("玩家2剩余").append(player2Cards.size()).append("张牌").toString());
        player3CardsText.setText(new StringBuilder("玩家3剩余").append(player3Cards.size()).append("张牌").toString());
        for (int i = 0; i < 52; ++i) {
            if (myCards.contains(i)) {
                playerCardsImage.get(i).setVisibility(View.VISIBLE);
                playerCardsImage.get(i).setEnabled(false);
            }
            else playerCardsImage.get(i).setVisibility(View.GONE);
        }

        currentPlayCards = new ArrayList<>();

        player1Play();
    }

    private void handlePass() {
		cnt_click_card = 0;

        for (int i = 0; i < 52; ++i)
            playImage.get(i).setVisibility(View.GONE);
		
        currentPlayer = (currentPlayer + 1) % 4;
        currentPlayCards = new ArrayList<>();

        //更新UI，设置按钮使能
        playButton.setEnabled(false);
        passButton.setEnabled(false);
        whoPlay.setText("你未出牌");
        whoseTurn.setText("目前轮到玩家1出牌");
        player1CardsText.setText(new StringBuilder("玩家1剩余").append(player1Cards.size()).append("张牌").toString());
        player2CardsText.setText(new StringBuilder("玩家2剩余").append(player1Cards.size()).append("张牌").toString());
        player3CardsText.setText(new StringBuilder("玩家3剩余").append(player1Cards.size()).append("张牌").toString());
        for (int i = 0; i < 52; ++i) {
            if (myCards.contains(i)) {
                playerCardsImage.get(i).setVisibility(View.VISIBLE);
                playerCardsImage.get(i).setEnabled(false);
            }
            else playerCardsImage.get(i).setVisibility(View.GONE);
        }

        player1Play();
    }

    private void player1Play() {
		whoPlay.setVisibility(View.VISIBLE);
		
        //通过联网程序得到玩家1出的牌
        //currentPlayCards = network();

        if (!currentPlayCards.isEmpty()) {
            for (Integer card : currentPlayCards) {
                player1Cards.remove(card);
                playImage.get(card).setVisibility(View.VISIBLE);
            }
        }
			
		currentPlayer = (currentPlayer + 1) % 4;

        //更新UI，设置按钮使能
        if (currentPlayCards.isEmpty())
            whoPlay.setText(new StringBuilder("玩家1出的牌为：").append(currentPlayCardsToString()).toString());
        else whoPlay.setText("玩家1未出牌");
        whoseTurn.setText("目前轮到玩家2出牌");
        player1CardsText.setText(new StringBuilder("玩家1剩余").append(player1Cards.size()).append("张牌").toString());
        player2CardsText.setText(new StringBuilder("玩家2剩余").append(player2Cards.size()).append("张牌").toString());
        player3CardsText.setText(new StringBuilder("玩家3剩余").append(player3Cards.size()).append("张牌").toString());

        currentPlayCards = new ArrayList<>();

        player2Play();
    }

    private void player2Play() {
        //通过联网程序得到玩家2出的牌
        //currentPlayCards = network();
		
		whoPlay.setVisibility(View.VISIBLE);

        if (!currentPlayCards.isEmpty()) {
            for (Integer card : currentPlayCards) {
                player2Cards.remove(card);
                playImage.get(card).setVisibility(View.VISIBLE);
            }
        }
			
		currentPlayer = (currentPlayer + 1) % 4;

        //更新UI，设置按钮使能
        if (currentPlayCards.isEmpty())
            whoPlay.setText(new StringBuilder("玩家2出的牌为：").append(currentPlayCardsToString()).toString());
        else whoPlay.setText("玩家2未出牌");
        whoseTurn.setText("目前轮到玩家3出牌");
        player1CardsText.setText(new StringBuilder("玩家1剩余").append(player1Cards.size()).append("张牌").toString());
        player2CardsText.setText(new StringBuilder("玩家2剩余").append(player2Cards.size()).append("张牌").toString());
        player3CardsText.setText(new StringBuilder("玩家3剩余").append(player3Cards.size()).append("张牌").toString());

        currentPlayCards = new ArrayList<>();

        player3Play();
    }

    private void player3Play() {
        //通过联网程序得到玩家3出的牌
        //currentPlayCards = network();
		
		whoPlay.setVisibility(View.VISIBLE);

        if (!currentPlayCards.isEmpty()) {
            for (Integer card : currentPlayCards) {
                player3Cards.remove(card);
                playImage.get(card).setVisibility(View.VISIBLE);
            }
        }
			
		currentPlayer = (currentPlayer + 1) % 4;

        //更新UI，设置按钮使能
        playButton.setEnabled(true);
        passButton.setEnabled(true);
        if (currentPlayCards.isEmpty())
            whoPlay.setText(new StringBuilder("玩家3出的牌为：").append(currentPlayCardsToString()).toString());
        else whoPlay.setText("玩家3未出牌");
        whoseTurn.setText("目前轮到你出牌");
        player1CardsText.setText(new StringBuilder("玩家1剩余").append(player1Cards.size()).append("张牌").toString());
        player2CardsText.setText(new StringBuilder("玩家2剩余").append(player2Cards.size()).append("张牌").toString());
        player3CardsText.setText(new StringBuilder("玩家3剩余").append(player3Cards.size()).append("张牌").toString());
        for (int i = 0; i < 52; ++i) {
            if (myCards.contains(i)) {
                playerCardsImage.get(i).setVisibility(View.VISIBLE);
                playerCardsImage.get(i).setEnabled(true);
            }
            else playerCardsImage.get(i).setVisibility(View.GONE);
        }

        currentPlayCards = new ArrayList<>();
    }
}
