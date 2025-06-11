package com.example.cdd.View;

import android.Manifest;
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

    private TextView tvPlayer1Name, tvPlayer2Name, tvPlayer3Name, tvPlayer4Name;
    private TextView tvPlayer1CardsCount, tvPlayer2CardsCount, tvPlayer3CardsCount, tvPlayer4CardsCount;
    private LinearLayout llPlayer1Hand; // 本地玩家手牌
    private LinearLayout llPlayer1Played; // 牌桌中央，显示最近打出的牌
    private LinearLayout llPlayer2Played, llPlayer3Played, llPlayer4Played; // 其他布局保留，不再用于显示牌
    private FrameLayout fragmentContainer;
    private Button btnPlayCards, btnPass, btnReady;
    private ImageView btnBack;
    private List<Card> selectedCards = new ArrayList<>();
    private List<ImageView> player1HandCardImageViews = new ArrayList<>();

    private Player currentPlayer; // 当前本地玩家对象 (从 GameState 中获取其手牌，仅房主维持 GameState)
    private int myPlayerIndex = -1; // 本地玩家的序号，房主为0，客户端为1-3，弃用
    private boolean isHost; // 标记当前设备是否是房主

    private List<Card> myHandCard=new ArrayList<>();

    private List<Card> LastPlayedCards = new ArrayList<>();


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
    }

    private void sendReadySignal() {

    }

    private void playSelectedCards() {
        if (selectedCards.isEmpty()) {
            Toast.makeText(getContext(), "请选择要出的牌", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isHost) { // 房主端直接调用控制器逻辑
            handlePlayerAction(selectedCards, null); // fromDevice 为 null
        } else { // 客户端发送选中的牌给房主
            if (bluetoothController != null) {
                bluetoothController.sendDataToServer(new ArrayList<>(selectedCards));
                Toast.makeText(getContext(), "已发送出牌请求，等待房主判断", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "已发送过牌请求，等待房主判断", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 房主端统一处理玩家行为的函数
    // fromDevice 用于错误信息私聊回复，如果是房主自己操作，则为 null
    private void handlePlayerAction(List<Card> cards, @Nullable BluetoothDevice fromDevice) {
        GameState gameState = GameState.getInstance();
        if (gameState == null) {
            Toast.makeText(getContext(), "游戏状态未初始化", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "出牌成功！", Toast.LENGTH_SHORT).show();

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

            Toast.makeText(getContext(), "出牌不合法", Toast.LENGTH_SHORT).show();
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
                player1HandCardImageViews.clear();

                if (myHandCard != null) {
                    for (Card card : myHandCard) {
                        ImageView cardImage = createCardImageView(card);
                        cardImage.setOnClickListener(v -> toggleCardSelection(card, cardImage));
                        if (selectedCards.contains(card)) {
                            cardImage.setTranslationY(-20);
                        } else {
                            cardImage.setTranslationY(0);
                        }
                        llPlayer1Hand.addView(cardImage);
                        player1HandCardImageViews.add(cardImage);
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

    private void toggleCardSelection(Card card, ImageView cardImage) {
        if (selectedCards.contains(card)) {
            selectedCards.remove(card);
            cardImage.setTranslationY(0); // 牌回到原位
        } else {
            selectedCards.add(card);
            cardImage.setTranslationY(-20); // 牌上移
        }
    }

    private void showGameEndDialog() {
        if (!isAdded()) return; // Fragment未附加到Activity

        updateUI(false,true); // 更新UI
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("游戏结束")
                .setMessage("游戏结束 " )
                .setNegativeButton("返回主菜单", (dialog, which) -> {
                    // 退出游戏，返回主菜单
                    handleExitGame();
                })
                .setCancelable(true); // 不允许点击对话框外部取消

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


            }  else if (data instanceof ArrayList &&!(myHandCard.isEmpty()) ) {
                LastPlayedCards=new ArrayList<>((ArrayList<Card>)data);
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
        player1HandCardImageViews.clear();
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
        new AlertDialog.Builder(requireContext())
                .setTitle("退出游戏")
                .setMessage("确定要退出当前游戏吗？这会结束本局游戏。")
                .setPositiveButton("确定", (dialog, which) -> {
                    navigateToMainPage();
                })
                .setNegativeButton("取消", null)
                .show();
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
            Toast.makeText(getContext(), "有人已退出游戏。", Toast.LENGTH_SHORT).show();
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
}