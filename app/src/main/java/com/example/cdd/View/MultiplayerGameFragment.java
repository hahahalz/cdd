package com.example.cdd.View;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cdd.Controller.BluetoothController;
import com.example.cdd.Controller.MutipleController;
import com.example.cdd.Model.Card;
import com.example.cdd.Model.GameState;
import com.example.cdd.Model.Player;
import com.example.cdd.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MultiplayerGameFragment extends Fragment implements BluetoothController.BluetoothListener {

    private BluetoothController bluetoothController;
    private MutipleController gameController;
    private List<Button> playerHandCardsButton;
    private List<ImageView> selectedCardsImage;
    private List<ImageView> lastPlayedCardsImage;

    private TextView tvPlayer1Name, tvPlayer2Name, tvPlayer3Name, tvPlayer4Name;
    private TextView tvPlayer1CardsCount, tvPlayer2CardsCount, tvPlayer3CardsCount, tvPlayer4CardsCount;
    private LinearLayout llPlayer1Hand; // 本地玩家手牌
    private LinearLayout llPlayer1Played; // 牌桌中央，显示最近打出的牌
    private LinearLayout llPlayer2Played, llPlayer3Played, llPlayer4Played; // 其他布局保留，不再用于显示牌
    private FrameLayout fragmentContainer;
    private Button btnPlayCards, btnPass, btnReady;
    private ImageView btnBack;
    private List<Card> selectedCards = new ArrayList<>();
    //private List<ImageView> player1HandCardImageViews = new ArrayList<>();

    private Player currentPlayer; // 当前本地玩家对象 (从 GameState 中获取其手牌，仅房主维持 GameState)
    private int myPlayerIndex = -1; // 本地玩家的序号，房主为0，客户端为1-3，弃用
    private boolean isHost; // 标记当前设备是否是房主

    private List<Card> myHandCard=new ArrayList<>();

    private List<Card> LastPlayedCards = new ArrayList<>();

    private List<Card>  wantToPlay=new ArrayList<>();

    private int minNeed=1;

    //状态码
    //0，到你出牌；1，操作失败；3，操作成功；4.玩家过牌;5，游戏结束;6.我退出；7.有人提前溜了



    public MultiplayerGameFragment(boolean isHost){
        this.isHost = isHost;
        gameController=new MutipleController();
    }

    public MultiplayerGameFragment(boolean isHost,int a){
        this.isHost = isHost;
        this.myPlayerIndex=a;
        gameController=new MutipleController();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            bluetoothController = ((MainActivity) context).getBluetoothController();
            if (bluetoothController != null) {
                bluetoothController.setListener(this);
                //isHost = bluetoothController.isserve();
                if (isHost) {
                    myPlayerIndex = 0; // 房主默认序号为0

                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multiplayer_game, container, false);

        initViews(view);
        setupListeners();

        btnPlayCards.setEnabled(false);
        btnPass.setEnabled(false);

        return view;
    }

    private void initViews(View view) {
        playerHandCardsButton = new ArrayList<>();

        playerHandCardsButton.add(view.findViewById(R.id.cluba));
        playerHandCardsButton.add(view.findViewById(R.id.club2));
        playerHandCardsButton.add(view.findViewById(R.id.club3));
        playerHandCardsButton.add(view.findViewById(R.id.club4));
        playerHandCardsButton.add(view.findViewById(R.id.club5));
        playerHandCardsButton.add(view.findViewById(R.id.club6));
        playerHandCardsButton.add(view.findViewById(R.id.club7));
        playerHandCardsButton.add(view.findViewById(R.id.club8));
        playerHandCardsButton.add(view.findViewById(R.id.club9));
        playerHandCardsButton.add(view.findViewById(R.id.club10));
        playerHandCardsButton.add(view.findViewById(R.id.clubj));
        playerHandCardsButton.add(view.findViewById(R.id.clubq));
        playerHandCardsButton.add(view.findViewById(R.id.clubk));

        playerHandCardsButton.add(view.findViewById(R.id.hearta));
        playerHandCardsButton.add(view.findViewById(R.id.heart2));
        playerHandCardsButton.add(view.findViewById(R.id.heart3));
        playerHandCardsButton.add(view.findViewById(R.id.heart4));
        playerHandCardsButton.add(view.findViewById(R.id.heart5));
        playerHandCardsButton.add(view.findViewById(R.id.heart6));
        playerHandCardsButton.add(view.findViewById(R.id.heart7));
        playerHandCardsButton.add(view.findViewById(R.id.heart8));
        playerHandCardsButton.add(view.findViewById(R.id.heart9));
        playerHandCardsButton.add(view.findViewById(R.id.heart10));
        playerHandCardsButton.add(view.findViewById(R.id.heartj));
        playerHandCardsButton.add(view.findViewById(R.id.heartq));
        playerHandCardsButton.add(view.findViewById(R.id.heartk));

        playerHandCardsButton.add(view.findViewById(R.id.diamonda));
        playerHandCardsButton.add(view.findViewById(R.id.diamond2));
        playerHandCardsButton.add(view.findViewById(R.id.diamond3));
        playerHandCardsButton.add(view.findViewById(R.id.diamond4));
        playerHandCardsButton.add(view.findViewById(R.id.diamond5));
        playerHandCardsButton.add(view.findViewById(R.id.diamond6));
        playerHandCardsButton.add(view.findViewById(R.id.diamond7));
        playerHandCardsButton.add(view.findViewById(R.id.diamond8));
        playerHandCardsButton.add(view.findViewById(R.id.diamond9));
        playerHandCardsButton.add(view.findViewById(R.id.diamond10));
        playerHandCardsButton.add(view.findViewById(R.id.diamondj));
        playerHandCardsButton.add(view.findViewById(R.id.diamondq));
        playerHandCardsButton.add(view.findViewById(R.id.diamondk));

        playerHandCardsButton.add(view.findViewById(R.id.spadea));
        playerHandCardsButton.add(view.findViewById(R.id.spade2));
        playerHandCardsButton.add(view.findViewById(R.id.spade3));
        playerHandCardsButton.add(view.findViewById(R.id.spade4));
        playerHandCardsButton.add(view.findViewById(R.id.spade5));
        playerHandCardsButton.add(view.findViewById(R.id.spade6));
        playerHandCardsButton.add(view.findViewById(R.id.spade7));
        playerHandCardsButton.add(view.findViewById(R.id.spade8));
        playerHandCardsButton.add(view.findViewById(R.id.spade9));
        playerHandCardsButton.add(view.findViewById(R.id.spade10));
        playerHandCardsButton.add(view.findViewById(R.id.spadej));
        playerHandCardsButton.add(view.findViewById(R.id.spadeq));
        playerHandCardsButton.add(view.findViewById(R.id.spadek));

        for (int i = 0; i < 52; ++i)
            playerHandCardsButton.get(i).setVisibility(View.GONE);



        selectedCardsImage = new ArrayList<>();

        selectedCardsImage.add(view.findViewById(R.id.i_cluba));
        selectedCardsImage.add(view.findViewById(R.id.i_club2));
        selectedCardsImage.add(view.findViewById(R.id.i_club3));
        selectedCardsImage.add(view.findViewById(R.id.i_club4));
        selectedCardsImage.add(view.findViewById(R.id.i_club5));
        selectedCardsImage.add(view.findViewById(R.id.i_club6));
        selectedCardsImage.add(view.findViewById(R.id.i_club7));
        selectedCardsImage.add(view.findViewById(R.id.i_club8));
        selectedCardsImage.add(view.findViewById(R.id.i_club9));
        selectedCardsImage.add(view.findViewById(R.id.i_club10));
        selectedCardsImage.add(view.findViewById(R.id.i_clubj));
        selectedCardsImage.add(view.findViewById(R.id.i_clubq));
        selectedCardsImage.add(view.findViewById(R.id.i_clubk));

        selectedCardsImage.add(view.findViewById(R.id.i_hearta));
        selectedCardsImage.add(view.findViewById(R.id.i_heart2));
        selectedCardsImage.add(view.findViewById(R.id.i_heart3));
        selectedCardsImage.add(view.findViewById(R.id.i_heart4));
        selectedCardsImage.add(view.findViewById(R.id.i_heart5));
        selectedCardsImage.add(view.findViewById(R.id.i_heart6));
        selectedCardsImage.add(view.findViewById(R.id.i_heart7));
        selectedCardsImage.add(view.findViewById(R.id.i_heart8));
        selectedCardsImage.add(view.findViewById(R.id.i_heart9));
        selectedCardsImage.add(view.findViewById(R.id.i_heart10));
        selectedCardsImage.add(view.findViewById(R.id.i_heartj));
        selectedCardsImage.add(view.findViewById(R.id.i_heartq));
        selectedCardsImage.add(view.findViewById(R.id.i_heartk));

        selectedCardsImage.add(view.findViewById(R.id.i_diamonda));
        selectedCardsImage.add(view.findViewById(R.id.i_diamond2));
        selectedCardsImage.add(view.findViewById(R.id.i_diamond3));
        selectedCardsImage.add(view.findViewById(R.id.i_diamond4));
        selectedCardsImage.add(view.findViewById(R.id.i_diamond5));
        selectedCardsImage.add(view.findViewById(R.id.i_diamond6));
        selectedCardsImage.add(view.findViewById(R.id.i_diamond7));
        selectedCardsImage.add(view.findViewById(R.id.i_diamond8));
        selectedCardsImage.add(view.findViewById(R.id.i_diamond9));
        selectedCardsImage.add(view.findViewById(R.id.i_diamond10));
        selectedCardsImage.add(view.findViewById(R.id.i_diamondj));
        selectedCardsImage.add(view.findViewById(R.id.i_diamondq));
        selectedCardsImage.add(view.findViewById(R.id.i_diamondk));

        selectedCardsImage.add(view.findViewById(R.id.i_spadea));
        selectedCardsImage.add(view.findViewById(R.id.i_spade2));
        selectedCardsImage.add(view.findViewById(R.id.i_spade3));
        selectedCardsImage.add(view.findViewById(R.id.i_spade4));
        selectedCardsImage.add(view.findViewById(R.id.i_spade5));
        selectedCardsImage.add(view.findViewById(R.id.i_spade6));
        selectedCardsImage.add(view.findViewById(R.id.i_spade7));
        selectedCardsImage.add(view.findViewById(R.id.i_spade8));
        selectedCardsImage.add(view.findViewById(R.id.i_spade9));
        selectedCardsImage.add(view.findViewById(R.id.i_spade10));
        selectedCardsImage.add(view.findViewById(R.id.i_spadej));
        selectedCardsImage.add(view.findViewById(R.id.i_spadeq));
        selectedCardsImage.add(view.findViewById(R.id.i_spadek));

        for (int i = 0; i < 52; ++i)
            selectedCardsImage.get(i).setVisibility(View.GONE);



        lastPlayedCardsImage = new ArrayList<>();

        lastPlayedCardsImage.add(view.findViewById(R.id.i2_cluba));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_club2));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_club3));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_club4));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_club5));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_club6));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_club7));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_club8));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_club9));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_club10));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_clubj));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_clubq));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_clubk));

        lastPlayedCardsImage.add(view.findViewById(R.id.i2_hearta));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_heart2));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_heart3));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_heart4));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_heart5));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_heart6));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_heart7));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_heart8));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_heart9));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_heart10));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_heartj));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_heartq));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_heartk));

        lastPlayedCardsImage.add(view.findViewById(R.id.i2_diamonda));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_diamond2));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_diamond3));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_diamond4));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_diamond5));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_diamond6));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_diamond7));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_diamond8));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_diamond9));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_diamond10));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_diamondj));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_diamondq));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_diamondk));

        lastPlayedCardsImage.add(view.findViewById(R.id.i2_spadea));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_spade2));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_spade3));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_spade4));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_spade5));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_spade6));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_spade7));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_spade8));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_spade9));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_spade10));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_spadej));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_spadeq));
        lastPlayedCardsImage.add(view.findViewById(R.id.i2_spadek));

        for (int i = 0; i < 52; ++i)
            lastPlayedCardsImage.get(i).setVisibility(View.GONE);



        tvPlayer1Name = view.findViewById(R.id.tv_player1_name);
        tvPlayer2Name = view.findViewById(R.id.tv_player2_name);
        tvPlayer3Name = view.findViewById(R.id.tv_player3_name);
        tvPlayer4Name = view.findViewById(R.id.tv_player4_name);

        tvPlayer1CardsCount = view.findViewById(R.id.tv_player1_cards_count);
        tvPlayer2CardsCount = view.findViewById(R.id.tv_player2_cards_count);
        tvPlayer3CardsCount = view.findViewById(R.id.tv_player3_cards_count);
        tvPlayer4CardsCount = view.findViewById(R.id.tv_player4_cards_count);

        llPlayer1Hand = view.findViewById(R.id.ll_player1_hand);
        llPlayer1Played = view.findViewById(R.id.ll_player1_played);
        llPlayer2Played = view.findViewById(R.id.ll_player2_played);
        llPlayer3Played = view.findViewById(R.id.ll_player3_played);
        llPlayer4Played = view.findViewById(R.id.ll_player4_played);

        btnPlayCards = view.findViewById(R.id.btn_play_cards);
        btnPass = view.findViewById(R.id.btn_pass);

        btnBack = view.findViewById(R.id.btn_back);
    }

    private void setupListeners() {
        btnPlayCards.setOnClickListener(v -> playSelectedCards());
        btnPass.setOnClickListener(v -> passTurn());
        //btnReady.setOnClickListener(v -> sendReadySignal());
        btnBack.setOnClickListener(v -> showExitConfirmationDialog());

        for (int i = 0; i < 52; ++i) {
            final Card card = integerToCard(i);
            playerHandCardsButton.get(i).setOnClickListener(v -> {
                if (selectedCards.contains(card)) {
                    selectedCards.remove(card);
                    selectedCardsImage.get(cardToInteger(card)).setVisibility(View.GONE);
                } else {
                    selectedCards.add(card);
                    selectedCardsImage.get(cardToInteger(card)).setVisibility(View.VISIBLE);
                }

                playerHandCardsButton.get(cardToInteger(card));

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

    private void sendReadySignal() {

    }

    private void playSelectedCards() {
        if (selectedCards.isEmpty()) {
            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "请选择要出的牌", Toast.LENGTH_SHORT).show());
            return;
        }

        if (isHost) { // 房主端直接调用控制器逻辑
            handlePlayerAction(selectedCards, null); // fromDevice 为 null
        } else { // 客户端发送选中的牌给房主
            if (bluetoothController != null) {
                bluetoothController.sendDataToServer(new ArrayList<>(selectedCards));
                wantToPlay=new ArrayList<>(selectedCards);
                requireActivity().runOnUiThread(() ->Toast.makeText(getContext(), "已发送出牌请求，等待房主判断", Toast.LENGTH_SHORT).show());

                //清除自己选择的牌图片
                for (int i = 0; i < 52; ++i)
                    selectedCardsImage.get(i).setVisibility(View.GONE);

                //把自己的牌呈现在上次选的牌区域
                for (int i = 0; i < 52; ++i)
                    lastPlayedCardsImage.get(i).setVisibility(View.GONE);
                for (Card card : selectedCards)
                    lastPlayedCardsImage.get(cardToInteger(card)).setVisibility(View.VISIBLE);

                selectedCards.clear();
                updateUI(false,false); // 立即更新UI，清除选中状态
            }
        }
    }

    private void passTurn() {
        if (isHost) { // 房主端直接调用控制器逻辑
            handPlaypass(0);
        } else { // 客户端发送空列表表示过牌给房主
            if (bluetoothController != null) {
                bluetoothController.sendDataToServer(new ArrayList<Card>()); // 发送空列表
                requireActivity().runOnUiThread(() ->Toast.makeText(getContext(), "已发送过牌请求，等待房主判断", Toast.LENGTH_SHORT).show());
            }
        }

        for (int i = 0; i < 52; ++i) {
            selectedCardsImage.get(i).setVisibility(View.GONE);
            lastPlayedCardsImage.get(i).setVisibility(View.GONE);
        }
    }

    // 房主端统一处理玩家行为的函数
    // fromDevice 用于错误信息私聊回复，如果是房主自己操作，则为 null
    private void handlePlayerAction(List<Card> cards, @Nullable BluetoothDevice fromDevice) {
        GameState gameState = GameState.getInstance();
        if (gameState == null) {
            requireActivity().runOnUiThread(() ->Toast.makeText(getContext(), "游戏状态未初始化", Toast.LENGTH_SHORT).show());
            // 如果是客户端的请求，告诉它失败
            if (fromDevice != null && bluetoothController != null) {
                bluetoothController.sendDataToClient(gameController.getNowIndex(), new ActionInvalidMessage("游戏状态未初始化。"));
            }
            return;
        }


        boolean actionSuccessful = gameController.playCard(cards);

        if (actionSuccessful) {
            if(fromDevice==null) {
                selectedCards.clear(); // 房主本地也清除选中牌
                requireActivity().runOnUiThread(() ->Toast.makeText(getContext(), "出牌成功！", Toast.LENGTH_SHORT).show());

                int winnerIndex = gameController.getWinnerIndex();
                bluetoothController.broadcastDataToClients((Serializable) cards);
                if (winnerIndex != -1) {

                    updateUI(false,true);
                    bluetoothController.broadcastDataToClients(5);
                }
                else
                    updateUI(false,false);
            }

            else
            {
                int winnerIndex = gameController.getWinnerIndex();
                bluetoothController.broadcastDataToClients((Serializable) cards);
                if (winnerIndex != -1) {

                    updateUI(false,true);
                    bluetoothController.broadcastDataToClients(5);
                }
                else
                    updateUI(false,false);

            }





        } else {

            if(fromDevice==null)
                requireActivity().runOnUiThread(() ->Toast.makeText(getContext(), "出牌不合法", Toast.LENGTH_SHORT).show());
            // 如果是客户端的请求，告诉它失败
            if (fromDevice != null && bluetoothController != null) {
                bluetoothController.sendDataToClient(gameController.getNowIndex(), 1);
            }
        }
    }

    // 房主向所有玩家广播当前轮次信息
    private void sendTurnInfoToAllPlayers(TurnInfoMessage message) {
        if (bluetoothController != null && getContext() != null && isHost) {
            bluetoothController.broadcastDataToClients(message);
            Log.d("MultiplayerGameFragment", "TurnInfoMessage sent. Current player: " + message.currentPlayerIndex);
        }
    }


    private void updateUI(boolean buttonsEnabled,boolean isGameOver) {
        if (!isAdded() || getContext() == null) {
            return;
        }
        requireActivity().runOnUiThread(() -> {

                // 清空自己手牌显示区域
                llPlayer1Hand.removeAllViews();

                //player1HandCardImageViews.clear();
                for (int i = 0; i < 52; ++i)
                    playerHandCardsButton.get(i).setVisibility(View.GONE);

                if (myHandCard != null) {
                    for (Card card : myHandCard) {
//                        ImageView cardImage = createCardImageView(card);
//                        if (selectedCards.contains(card)) {
//                            cardImage.setTranslationY(-20);
//                        } else {
//                            cardImage.setTranslationY(0);
//                        }
//                        llPlayer1Hand.addView(cardImage);
                        playerHandCardsButton.get(cardToInteger(card)).setVisibility(View.VISIBLE);
                    }
                    tvPlayer1CardsCount.setText("手牌: " + myHandCard.size());
                } else {
                    tvPlayer1CardsCount.setText("手牌: 0");
                }
                tvPlayer1Name.setText("我" );


            // 更新牌桌中央区显示最新打出的牌
            llPlayer1Played.removeAllViews();
            if (LastPlayedCards != null && !LastPlayedCards.isEmpty()) {
                for (Card card : LastPlayedCards) {
                    ImageView cardImage = createCardImageView(card);
                    llPlayer1Played.addView(cardImage);
                }
            }

            // 清空其他玩家的已出牌区域
            llPlayer2Played.removeAllViews();
            llPlayer3Played.removeAllViews();
            llPlayer4Played.removeAllViews();

            // 根据传入的布尔值参数控制按钮可用性
            btnPlayCards.setEnabled(buttonsEnabled);
            btnPass.setEnabled(buttonsEnabled);

            if (isGameOver) {


                showGameEndDialog();
                btnPlayCards.setEnabled(false);
                btnPass.setEnabled(false);
                btnReady.setEnabled(true);
            }
        });
    }

    private ImageView createCardImageView(Card card) {
        ImageView imageView = new ImageView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(50), // 牌的宽度
                dpToPx(70)  // 牌的高度
        );
        params.setMargins(0, 0, dpToPx(4), 0); // 右边距
        imageView.setLayoutParams(params);

        String cardResourceName = getCardResourceName(card);
        int resId = getResources().getIdentifier(cardResourceName, "drawable", requireContext().getPackageName());

            imageView.setImageResource(resId);

        return imageView;
    }

    private String getCardResourceName(Card card) {
        String rank = card.getRank().name().toLowerCase();
        String suit = card.getSuit().name().toLowerCase();
        // 特殊处理 10, J, Q, K, A, 2
        if (card.getRank() == Card.Rank.TEN) rank = "t";
        else if (card.getRank() == Card.Rank.JACK) rank = "j";
        else if (card.getRank() == Card.Rank.QUEEN) rank = "q";
        else if (card.getRank() == Card.Rank.KING) rank = "k";
        else if (card.getRank() == Card.Rank.ACE) rank = "a";
        else if (card.getRank() == Card.Rank.TWO) rank = "2";
        else rank = String.valueOf(card.getRank().getValue()); // 3-9

        return suit + "_" + rank;
    }


    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void showGameEndDialog() {
        if (!isAdded()) return; // Fragment未附加到Activity

        updateUI(false,true); // 更新UI
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        requireActivity().runOnUiThread(() ->builder.setTitle("游戏结束")
                .setMessage("游戏结束 " )
                .setNegativeButton("返回主菜单", (dialog, which) -> {
                    // 退出游戏，返回主菜单
                    handleExitGame();
                })
                .setCancelable(true)); // 不允许点击对话框外部取消

    }


    @Override
    public void onDataReceived(BluetoothDevice fromDevice, Object data) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "蓝牙连接权限不足，无法显示发送方设备名称", Toast.LENGTH_SHORT).show());
            return;
        }



        if (isHost) { // 房主端接收数据
            if (data instanceof ArrayList)
            { // 收到客户端的出牌/过牌请求
                ArrayList<Card> clientCards = (ArrayList<Card>) data;

                // 房主直接调用 handlePlayerAction 来处理客户端的请求
                if(!clientCards.isEmpty())
                    handlePlayerAction(clientCards, fromDevice);

                else
                    handPlaypass(gameController.getNowIndex());//过牌

            }

        } else { // 客户端接收数据
            if (data instanceof ArrayList&&myHandCard.isEmpty()) {

            myHandCard=new ArrayList<>((ArrayList<Card>)data);

                requireActivity().runOnUiThread(() -> {for (Card card : myHandCard)
                playerHandCardsButton.get(cardToInteger(card)).setVisibility(View.VISIBLE);});

            }  else if (data instanceof ArrayList &&!(myHandCard.isEmpty()) ) {
                LastPlayedCards=new ArrayList<>((ArrayList<Card>)data);

                requireActivity().runOnUiThread(() ->
                {for (int i = 0; i < 52; ++i)
                    lastPlayedCardsImage.get(i).setVisibility(View.GONE);
                for(Card card : LastPlayedCards)
                    lastPlayedCardsImage.get(cardToInteger(card)).setVisibility(View.VISIBLE);
                });


                updateUI(false,false); // 更新UI
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "手牌已更新！", Toast.LENGTH_SHORT).show());
            }
            else if(data.equals(Integer.valueOf(0)))
            {
                updateUI(true,false);
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "到你出牌！", Toast.LENGTH_LONG).show());
            }
            else if(data.equals(Integer.valueOf(1)))
            {
                updateUI(true,false);
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "操作不合法！", Toast.LENGTH_LONG).show());
            }
            else if(data.equals(Integer.valueOf(3)))
            {
                if(!wantToPlay.isEmpty())
                {
                    for (int i=0;i<wantToPlay.size();i++){
                        for (int j=0;j<myHandCard.size();j++){
                            if (wantToPlay.get(i).equals(myHandCard.get(j))){
                                myHandCard.remove(j);
                                break;
                            }
                        }
                    }
                }
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "操作成功！", Toast.LENGTH_LONG).show());
            }
            else if(data.equals(Integer.valueOf(4)))
            {

                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "该玩家过牌！", Toast.LENGTH_LONG).show());
            }
            else if (data.equals(Integer.valueOf(5))) {

                updateUI(false,true); // 触发游戏结束对话框
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "游戏结束！", Toast.LENGTH_LONG).show());
            }
            else {

            }
        }
    }


    private void checkAllPlayersReady() {
        if (isHost && bluetoothController != null && bluetoothController.connectedClients.size() == minNeed) {

            bluetoothController.setDeviceByIndex();
            Log.d("MultiplayerGameFragment", "所有玩家已准备好，开始游戏...");
            startGame();
        }
    }


    private void startGame() {
        if (isHost) {
            List<List<Card>> all= gameController.dealCards(); // MutipleController.dealCards 应该已经更新了 GameState 内部的玩家手牌

            Log.d("MultiplayerGameFragment", "发牌");
            myHandCard=new ArrayList<>(all.get(0));

            requireActivity().runOnUiThread(() ->
            {for (Card card : myHandCard)
                playerHandCardsButton.get(cardToInteger(card)).setVisibility(View.VISIBLE);}
            );

            updateUI(false,false); // 更新UI以显示发牌后的手牌

            // 游戏开始时，房主广播初始 TurnInfoMessage
            System.out.println(bluetoothController.Clients);

            // 同时，房主需要单独向每个客户端发送其自己的手牌列表
            for (int i = 1; i <=minNeed; i++) {
                if (i != myPlayerIndex) { // 不给自己发
                        bluetoothController.sendDataToClient(i, new ArrayList<>(all.get(i)));
                        Log.d("MultiplayerGameFragment", "Sent initial hand cards to player " + i);

                }
            }

            bluetoothController.sendDataToClient(1,0);

            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "游戏开始！", Toast.LENGTH_LONG).show());

        }
    }

    @Override
    public void onDeviceDiscovered(BluetoothDevice device) { }

    @Override
    public void onDiscoveryFinished(List<BluetoothDevice> devices) { }

    @Override
    public void onServerStarted() {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "服务端已启动，等待连接...", Toast.LENGTH_SHORT).show());
        isHost = true;
        myPlayerIndex = 0; // 房主玩家序号固定为0
        if (gameController == null) {
            gameController = new MutipleController();
            // 房主自己的 Player 对象应该在 MutipleController 内部 Players 列表的 index 0
        }

    }

    @Override
    public void onClientConnected(BluetoothDevice device, boolean isServer) {
        checkAllPlayersReady();
    }

    @Override
    public void onClientDisconnected(BluetoothDevice device) {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), (  device.getAddress()) + " 已断开连接", Toast.LENGTH_SHORT).show());
        if (isHost) {


        } else {
            // 如果断开连接的是房主，客户端需要提示游戏中断或尝试重连

            requireActivity().runOnUiThread(() ->  new AlertDialog.Builder(requireContext())
                    .setTitle("连接断开")
                    .setMessage("房主已断开连接，游戏结束。")
                    .setPositiveButton("确定", (dialog, which) -> {
                        if (getActivity() instanceof MainActivity) {
                            // ((MainActivity) getActivity()).na(); // 返回主界面
                        }
                    })
                    .setCancelable(false)
                    .show());

        }
    }

    @Override
    public void onError(String message) {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "蓝牙错误: " + message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onLog(String message) {
        Log.d("BluetoothController_Log", message);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bluetoothController != null) {
            bluetoothController.setListener(this);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        selectedCards.clear();
        //player1HandCardImageViews.clear();
    }

    // 新增：表示轮次信息的类
    public static class TurnInfoMessage implements Serializable {
        public int currentPlayerIndex; // 当前轮到谁出牌的玩家序号
        public ArrayList<Card> lastPlayedCards; // 牌桌上最近打出的牌（客户端需要显示）
        public boolean isGameOver; // 游戏是否结束
        public int winnerIndex; // 赢家索引

        public TurnInfoMessage(int currentPlayerIndex, ArrayList<Card> lastPlayedCards, boolean isGameOver, int winnerIndex) {
            this.currentPlayerIndex = currentPlayerIndex;
            this.lastPlayedCards = lastPlayedCards;
            this.isGameOver = isGameOver;
            this.winnerIndex = winnerIndex;
        }
    }

    // 新增：表示操作无效的私有消息类
    public static class ActionInvalidMessage implements Serializable {
        public String message; // 错误信息

        public ActionInvalidMessage(String message) {
            this.message = message;
        }
    }



    private void showExitConfirmationDialog() {
        if (!isAdded()) return; // 确保 Fragment 附加到 Activity
        requireActivity().runOnUiThread(() ->new AlertDialog.Builder(requireContext())
                .setTitle("退出游戏")
                .setMessage("确定要退出当前游戏吗？这会结束本局游戏。")
                .setPositiveButton("确定", (dialog, which) -> {
                    navigateToMainPage();
                })
                .setNegativeButton("取消", null)
                .show());
    }

    private void handleExitGame() {
        if (isHost) {
            if (bluetoothController != null) {

                bluetoothController.broadcastDataToClients(7);
                bluetoothController.stopServer();
            }
            if (gameController != null) {
                gameController.quitGame(); // 调用 MutipleController 的退出方法
            }
        } else {
            if (bluetoothController != null) {
                bluetoothController.sendDataToServer(6);
                bluetoothController.disconnectClient();
            }
        }

        if (getActivity() instanceof MainActivity) { // 假设你的主 Activity 是 MainActivity
            navigateToMainPage(); // 导航回主Fragment
            requireActivity().runOnUiThread(() ->Toast.makeText(getContext(), "有人已退出游戏。", Toast.LENGTH_SHORT).show());
        }
    }

    private void navigateToMainPage() {
        if (fragmentContainer == null) {
            fragmentContainer = getActivity().findViewById(R.id.framelayout);
        }
        // 实现跳转到主页面的逻辑
        if (fragmentContainer != null ) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
            fragmentContainer.setClickable(false); // 禁用点击拦截
            fragmentContainer.setVisibility(View.INVISIBLE); // 隐藏容器（仍保留布局空间）
        }
    }

    private void handPlaypass(int i)
    {
        boolean enPass= gameController.pass();

        if(i==0)
        {
            if (enPass)
            {
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "过牌成功" , Toast.LENGTH_SHORT).show());
                bluetoothController.broadcastDataToClients(4);
            }
            else
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "过牌失败" , Toast.LENGTH_SHORT).show());
        }
        else
        {
            if (enPass)
                bluetoothController.broadcastDataToClients(4);
            else
                bluetoothController.sendDataToClient(i, 1);
        }
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
}
