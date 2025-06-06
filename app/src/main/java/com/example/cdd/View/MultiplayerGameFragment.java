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
import com.example.cdd.Pojo.PlayerInformation;
import com.example.cdd.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private int myPlayerIndex = -1; // 本地玩家的序号，房主为0，客户端为1-3
    private boolean isHost; // 标记当前设备是否是房主

    // 客户端需要知道最新的牌组来判断是否能出牌，即使不存储完整GameState
    private List<Card> clientLastPlayedCards = new ArrayList<>();
    private int clientCurrentPlayerIndex = -1; // 客户端需要知道当前轮到谁，才能判断是否是自己

    public MultiplayerGameFragment(boolean isHost){
        this.isHost = isHost;
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

        if (isHost) {
            gameController = new MutipleController();
            // 房主玩家对象应从MutipleController初始化后的GameState中获取
            if (GameState.getInstance() != null && myPlayerIndex != -1 && myPlayerIndex < GameState.getInstance().getPlayers().size()) {
                currentPlayer = (Player) GameState.getInstance().getPlayers().get(myPlayerIndex);
                clientCurrentPlayerIndex = GameState.getInstance().getCurrentPlayerIndex(); // 房主初始化自己的当前玩家索引
            } else {
                currentPlayer = new Player(PlayerInformation.getThePlayerInformation()); // 临时ID，等待GameState同步
            }
        } else {
            currentPlayer = new Player(PlayerInformation.getThePlayerInformation()); // 客户端玩家，临时ID
        }



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
        btnReady = view.findViewById(R.id.btn_ready);
        btnBack = view.findViewById(R.id.btn_back);
    }

    private void setupListeners() {
        btnPlayCards.setOnClickListener(v -> playSelectedCards());
        btnPass.setOnClickListener(v -> passTurn());
        btnReady.setOnClickListener(v -> sendReadySignal());
        btnBack.setOnClickListener(v -> showExitConfirmationDialog());
    }

    private void sendReadySignal() {
        if (myPlayerIndex != -1) {
            String readyMessage = "READY_SIGNAL:" + myPlayerIndex;
            if (bluetoothController != null) {
                if (isHost) {
                    bluetoothController.broadcastDataToClients((Serializable) readyMessage);
                } else {
                    bluetoothController.sendDataToServer((Serializable) readyMessage);
                }
                Toast.makeText(getContext(), "已发送准备信号", Toast.LENGTH_SHORT).show();
                btnReady.setEnabled(false); // 准备后禁用按钮
            }
        } else {
            Toast.makeText(getContext(), "玩家序号未确定，无法发送准备信号", Toast.LENGTH_SHORT).show();
        }
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
                updateUI(); // 立即更新UI，清除选中状态
            }
        }
    }

    private void passTurn() {
        if (isHost) { // 房主端直接调用控制器逻辑
            handlePlayerAction(new ArrayList<>(), null); // 发送空列表表示过牌, fromDevice 为 null
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
                bluetoothController.sendDataToClient(fromDevice.getAddress(), new ActionInvalidMessage("游戏状态未初始化。"));
            }
            return;
        }

        // 确保是轮到当前玩家出牌，这个逻辑应该在 MutipleController 内部判断，这里只是双重检查
        // 如果 fromDevice 不为 null，说明是客户端请求，需要先确定是哪个玩家的请求
        // 假设 MutipleController 内部会根据当前轮次和请求发起者来判断
        boolean actionSuccessful = false;
        if (cards.isEmpty()) { // 过牌
            actionSuccessful = gameController.pass();
        } else { // 出牌
            actionSuccessful = gameController.playCard(cards);
        }

        if (actionSuccessful) {
            selectedCards.clear(); // 房主本地也清除选中牌
            Toast.makeText(getContext(), (cards.isEmpty() ? "过牌" : "出牌") + "成功！", Toast.LENGTH_SHORT).show();

            int winnerIndex = gameController.getWinnerIndex();
            if (winnerIndex != -1) {
                showGameEndDialog("玩家 " + winnerIndex);
            }

            // 操作成功后，广播下一个轮到出牌的玩家序号和最新的牌桌牌
            // 房主更新自己的 UI 状态
            clientCurrentPlayerIndex = gameState.getCurrentPlayerIndex(); // 房主维护自己的当前玩家索引
            clientLastPlayedCards = new ArrayList<>(gameState.getLastPlayedCards()); // 房主也更新牌桌上的牌
            updateUI();

            // 广播下一个轮到出牌的玩家序号和最新的牌桌牌
            TurnInfoMessage turnMessage = new TurnInfoMessage(
                    gameState.getCurrentPlayerIndex(),
                    new ArrayList<>(gameState.getLastPlayedCards()),
                    gameState.isGameOver(),
                    winnerIndex
            );
            sendTurnInfoToAllPlayers(turnMessage);

        } else {
            String errorMessage = (cards.isEmpty() ? "无法过牌" : "出牌不符合规则") + "，请重新选择。";
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            // 如果是客户端的请求，告诉它失败
            if (fromDevice != null && bluetoothController != null) {
                bluetoothController.sendDataToClient(fromDevice.getAddress(), new ActionInvalidMessage(errorMessage));
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


    private void updateUI() {
        if (!isAdded() || getContext() == null) {
            return;
        }
        requireActivity().runOnUiThread(() -> {
            // 注意：客户端不再拥有完整的 GameState 对象副本，只通过收到的 TurnInfoMessage 更新必要信息
            // 房主依然使用 GameState.getInstance()

            // 更新本地玩家（玩家1）的手牌和信息
            // 无论是房主还是客户端，本地玩家的手牌数量和名称始终需要更新
            if (myPlayerIndex != -1) {
                // 客户端需要从某个地方获取自己的手牌信息。
                // 最简单的方式是房主在发送 TurnInfoMessage 的同时，也为每个客户端定制发送它自己的手牌信息。
                // 但为了遵循“不广播GameState，其他玩家不存储GameState”的要求，
                // 客户端无法直接从一个共享的 GameState 获取自己的手牌。
                //
                // **解决方案：** 房主在每次成功操作后，除了广播 TurnInfoMessage，还需要**单独向每个客户端发送其更新后的手牌列表。**
                // 暂时，我们假设 `currentPlayer.getHandCards()` 在房主端是有效的，
                // 在客户端，`currentPlayer` 的手牌数据会滞后，除非房主主动发送。
                // 为了演示，我将保留 `currentPlayer.getHandCards()`，但请注意客户端需要独立的机制来更新它。
                //
                // **替代方案（更符合你的精简要求）：** 客户端永远只显示“手牌: X张”，不显示具体牌面，除非房主专门发来手牌更新。
                // 在这里，我假设 `currentPlayer` 会在房主端通过 `GameState` 自动更新手牌，
                // 而客户端的 `currentPlayer` 的手牌数据可能需要单独的通信机制。
                // 为了不改动 Model，我们暂时让客户端的 `currentPlayer` 手牌可能不是最新的，直到房主明确发送。
                // 最简单的实现是客户端收到 TurnInfoMessage 后，假设自己的手牌已更新，只更新数量。
                // 但是，如果客户端需要选择牌来出，它的手牌 UI 必须是准确的。

                // 如果是房主，直接从 GameState 获取
                if (isHost && GameState.getInstance() != null && myPlayerIndex < GameState.getInstance().getPlayers().size()) {
                    currentPlayer = (Player) GameState.getInstance().getPlayers().get(myPlayerIndex);
                }
                // 客户端的 currentPlayer 手牌数据是滞后的，除非房主单独发送
                // 这里暂时只更新 UI 显示，实际手牌数据更新需要在 onDataReceived 中处理房主发送的个人手牌数据。
                llPlayer1Hand.removeAllViews();
                player1HandCardImageViews.clear();

                // 只有房主可以从 GameState 获取并显示具体手牌
                if (isHost && currentPlayer.getHandCards() != null) {
                    for (Card card : currentPlayer.getHandCards()) {
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
                    tvPlayer1CardsCount.setText("手牌: " + currentPlayer.getHandCards().size());
                } else if (!isHost) { // 客户端只显示手牌数量，不显示具体牌面
                    // 客户端需要从房主那里接收自己的手牌数量
                    // 这里假设 currentPlayer.getHandCards() 数量是最新收到的
                    tvPlayer1CardsCount.setText("手牌: " + currentPlayer.getHandCards().size()); // 客户端的 currentPlayer 手牌数量需要由房主私发
                } else { // 房主但手牌为空
                    tvPlayer1CardsCount.setText("手牌: 0");
                }
                tvPlayer1Name.setText("玩家 " + myPlayerIndex + (isHost ? " (房主)" : ""));
            } else {
                tvPlayer1Name.setText("玩家 (未初始化)");
                tvPlayer1CardsCount.setText("手牌: 0");
            }

            // 更新牌桌中央区（llPlayer1Played）显示最新打出的牌
            llPlayer1Played.removeAllViews();
            List<Card> displayedLastPlayed = isHost ?
                    (GameState.getInstance() != null ? GameState.getInstance().getLastPlayedCards() : null) :
                    clientLastPlayedCards; // 客户端使用自己维护的 clientLastPlayedCards
            if (displayedLastPlayed != null && !displayedLastPlayed.isEmpty()) {
                for (Card card : displayedLastPlayed) {
                    ImageView cardImage = createCardImageView(card);
                    llPlayer1Played.addView(cardImage);
                }
            }

            // 清空其他玩家的已出牌区域
            llPlayer2Played.removeAllViews();
            llPlayer3Played.removeAllViews();
            llPlayer4Played.removeAllViews();

            // 更新其他玩家信息 (根据序号来显示)
            TextView[] playerNamesViews = {tvPlayer1Name, tvPlayer2Name, tvPlayer3Name, tvPlayer4Name};
            TextView[] playerCardsCountViews = {tvPlayer1CardsCount, tvPlayer2CardsCount, tvPlayer3CardsCount, tvPlayer4CardsCount};

            // 房主从 GameState 获取所有玩家信息，客户端需要房主定期发送所有玩家的牌数信息
            // 简化处理：假设房主会发送一个包含所有玩家牌数的特殊消息，或者客户端的 playerCounts 数组会单独更新
            // 为了保持“客户端不储存GameState”的原则，其他玩家的牌数信息需要房主单独发送。
            // 暂时，这里只显示玩家名称和索引，牌数可能不准确。
            int totalPlayers = isHost ? GameState.getInstance().getPlayers().size() : 4; // 假设有4个玩家

            for (int i = 0; i < totalPlayers; i++) {
                int uiDisplayPosition = -1;

                if (i == myPlayerIndex) {
                    uiDisplayPosition = 0;
                } else {
                    uiDisplayPosition = (i - myPlayerIndex + 4) % 4;
                }

                if (uiDisplayPosition >= 0 && uiDisplayPosition < 4 && uiDisplayPosition != 0) {
                    playerNamesViews[uiDisplayPosition].setText("玩家 " + i + (i == 0 && isHost ? " (房主)" : ""));
                    // 客户端无法获取其他玩家的实时手牌数量，除非房主广播
                    // 这里只是占位符，实际值需要从房主那里的单独消息更新
                    playerCardsCountViews[uiDisplayPosition].setText("手牌: ?");
                }
            }


            // 检查游戏是否结束 (房主从 GameState 获取，客户端从 TurnInfoMessage 获取)
            boolean isGameOver = isHost ? GameState.getInstance().isGameOver() : (clientCurrentPlayerIndex == -2); // 约定 -2 为游戏结束信号
            int winnerIndex = isHost ? gameController.getWinnerIndex() : -1; // 客户端不知道赢家，除非 TurnInfoMessage 包含

            if (isGameOver) {
                String winnerName = (isHost && winnerIndex != -1) ? "玩家 " + winnerIndex : "未知";
                showGameEndDialog(winnerName);
                btnPlayCards.setEnabled(false);
                btnPass.setEnabled(false);
                btnReady.setEnabled(true);
            } else {
                // 根据当前轮到谁出牌来启用/禁用按钮 (房主和客户端都使用 clientCurrentPlayerIndex)
                if (clientCurrentPlayerIndex == myPlayerIndex) {
                    btnPlayCards.setEnabled(true);
                    btnPass.setEnabled(true);
                    Toast.makeText(getContext(), "轮到你出牌了！", Toast.LENGTH_SHORT).show();
                } else {
                    btnPlayCards.setEnabled(false);
                    btnPass.setEnabled(false);
                }
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

    private void showGameEndDialog(String winnerName) {
        if (!isAdded()) return; // Fragment未附加到Activity
        new AlertDialog.Builder(requireContext())
                .setTitle("游戏结束")
                .setMessage(winnerName + " 赢得了本轮游戏！")
                .setPositiveButton("确定", (dialog, which) -> {
                    if (isHost && gameController != null) {
                        gameController.endGame(); // 房主处理游戏结束逻辑
                        gameController.nextRound(); // 选择下一轮，会重新发牌
                        // 游戏结束后，房主广播 TurnInfoMessage 表示游戏结束或下一轮开始
                        sendTurnInfoToAllPlayers(new TurnInfoMessage(-2, new ArrayList<>(), true, -1)); // -2 约定为游戏结束
                    }
                    updateUI(); // 更新UI
                })
                .setCancelable(false)
                .show();
    }


    @Override
    public void onDataReceived(BluetoothDevice fromDevice, Object data) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "蓝牙连接权限不足，无法显示发送方设备名称", Toast.LENGTH_SHORT).show());
            return;
        }

        String deviceName = (fromDevice != null && fromDevice.getName() != null) ? fromDevice.getName() : fromDevice.getAddress();

        if (isHost) { // 房主端接收数据
            if (data instanceof ArrayList) { // 收到客户端的出牌/过牌请求
                ArrayList<Card> clientCards = (ArrayList<Card>) data;
                Log.d("MultiplayerGameFragment", "房主收到来自 " + deviceName + " 的牌组请求，数量: " + clientCards.size());
                // 房主需要知道这是哪个客户端发来的请求，以便在控制器中模拟该玩家操作
                // 这需要你有一个机制将 BluetoothDevice 映射到 myPlayerIndex
                // 暂时假设 MutipleController 内部会根据 GameState.getCurrentPlayerIndex() 来判断并处理
                // 如果 GameState.getCurrentPlayerIndex() 不是发起请求的玩家，房主应该拒绝
                // **重要：** MutipleController 需要知道当前轮到谁出牌。你需要确保在处理客户端请求之前，
                // MutipleController/GameState 中的 `currentPlayerIndex` 是正确的。
                // 客户端的 myPlayerIndex 需要在发送请求时包含，或者房主通过 BluetoothDevice 查找
                // 这里我们假设游戏状态已同步，`GameState.getCurrentPlayerIndex()` 就是发起请求的玩家。

                // 房主直接调用 handlePlayerAction 来处理客户端的请求
                handlePlayerAction(clientCards, fromDevice);

            } else if (data instanceof String && ((String) data).startsWith("READY_SIGNAL:")) {
                int senderIndex = Integer.parseInt(((String) data).substring(13));
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "玩家 " + senderIndex + " 已准备", Toast.LENGTH_SHORT).show());
                Log.d("MultiplayerGameFragment", "Player " + senderIndex + " sent READY signal.");
                // 房主需要追踪每个玩家的准备状态
                checkAllPlayersReady();
            }
            // 房主不应该收到 TurnInfoMessage 或 ActionInvalidMessage，除非是自己给自己发测试
        } else { // 客户端接收数据
            if (data instanceof TurnInfoMessage) {
                TurnInfoMessage turnMessage = (TurnInfoMessage) data;
                Log.d("MultiplayerGameFragment", "客户端收到 TurnInfoMessage. Current player: " + turnMessage.currentPlayerIndex);

                clientCurrentPlayerIndex = turnMessage.currentPlayerIndex;
                clientLastPlayedCards = turnMessage.lastPlayedCards;

                // 客户端需要知道自己的手牌数量是否更新。
                // **重要：** 如果客户端不存储GameState，房主需要单独发送每个客户端自己的最新手牌数据。
                // 这里我们假设房主会发送一个包含所有玩家手牌数量的公共消息，或者客户端的 `currentPlayer` 手牌数据会同步更新。
                // 暂时，我们假设 `currentPlayer` 的手牌数量可以从 `TurnInfoMessage` 中获取，或者客户端只显示“手牌：？”
                // 更好的方法是房主私发每个客户端自己的手牌，或者发送一个包含所有玩家手牌数量的公共信息。
                if (turnMessage.isGameOver) {
                    clientCurrentPlayerIndex = -2; // 约定 -2 为游戏结束信号
                    updateUI(); // 触发游戏结束对话框
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "游戏结束！", Toast.LENGTH_LONG).show());
                } else {
                    updateUI(); // 更新UI以反映当前轮次和牌桌牌
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "轮到玩家 " + clientCurrentPlayerIndex + " 出牌", Toast.LENGTH_SHORT).show());
                }

            } else if (data instanceof ActionInvalidMessage) {
                ActionInvalidMessage errorMessage = (ActionInvalidMessage) data;
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "出牌/过牌失败: " + errorMessage.message, Toast.LENGTH_LONG).show();
                    // 重新启用按钮，让玩家可以再次尝试
                    btnPlayCards.setEnabled(true);
                    btnPass.setEnabled(true);
                });
            } else if (data instanceof String && ((String) data).startsWith("ASSIGN_INDEX:")) {
                if (myPlayerIndex == -1) { // 确保是客户端且尚未设置序号
                    myPlayerIndex = Integer.parseInt(((String) data).substring(13));
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "你被分配为玩家 " + myPlayerIndex, Toast.LENGTH_LONG).show());
                    // 客户端此时也需要初始化自己的 currentPlayer 实例
                    currentPlayer = new Player(PlayerInformation.getThePlayerInformation()); // 使用分配的序号
                    Log.d("MultiplayerGameFragment", "My player index is set to: " + myPlayerIndex);
                    updateUI(); // 序号设置后更新UI
                }
            } else if (data instanceof String && ((String) data).startsWith("READY_SIGNAL:")) {
                int senderIndex = Integer.parseInt(((String) data).substring(13));
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "玩家 " + senderIndex + " 已准备", Toast.LENGTH_SHORT).show());
            } else if (data instanceof ArrayList && ((ArrayList) data).get(0) instanceof Card) {
                // 这是房主发来的客户端自己的手牌更新
                // 假设房主会私发每个客户端自己的最新手牌。
                ArrayList<Card> newHandCards = (ArrayList<Card>) data;
                Log.d("MultiplayerGameFragment", "客户端收到自己的手牌更新，数量: " + newHandCards.size());
                currentPlayer.setHandCards(newHandCards); // 更新客户端本地的 currentPlayer 手牌
                updateUI(); // 更新UI
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "手牌已更新！", Toast.LENGTH_SHORT).show());
            }
            else {
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "收到来自 " + deviceName + " 的未知数据", Toast.LENGTH_SHORT).show());
                Log.w("MultiplayerGameFragment", "Received unknown object type from " + deviceName);
            }
        }
    }


    private void checkAllPlayersReady() {
        if (isHost && bluetoothController != null && bluetoothController.connectedClients.size() == 3) {
            // 【待完成】这里需要一个更健壮的机制来追踪所有连接玩家的准备状态
            // 房主应该在 MutipleController 内部维护一个 ready 状态的 Map<Integer, Boolean>
            // 当所有玩家都 ready 后，调用 startGame()
            bluetoothController.setDeviceByIndex();
            Log.d("MultiplayerGameFragment", "所有玩家已准备好，开始游戏...");
            startGame();
        }
    }


    private void startGame() {
        if (isHost && gameController != null) {
            gameController.dealCards(); // MutipleController.dealCards 应该已经更新了 GameState 内部的玩家手牌

            // 房主更新自己的手牌
            if (myPlayerIndex != -1 && myPlayerIndex < GameState.getInstance().getPlayers().size()) {
                currentPlayer = (Player) GameState.getInstance().getPlayers().get(myPlayerIndex);
            } else {
                Log.e("MultiplayerGameFragment", "房主玩家序号未设置或超出范围，无法获取手牌。");
            }

            // 房主更新客户端的当前玩家索引和牌桌牌
            clientCurrentPlayerIndex = GameState.getInstance().getCurrentPlayerIndex();
            clientLastPlayedCards = new ArrayList<>(GameState.getInstance().getLastPlayedCards());

            updateUI(); // 更新UI以显示发牌后的手牌

            // 游戏开始时，房主广播初始 TurnInfoMessage
            TurnInfoMessage initialTurn = new TurnInfoMessage(
                    GameState.getInstance().getCurrentPlayerIndex(),
                    new ArrayList<>(GameState.getInstance().getLastPlayedCards()),
                    false, // 游戏未结束
                    -1 // 无赢家
            );
            sendTurnInfoToAllPlayers(initialTurn);

            // 同时，房主需要单独向每个客户端发送其自己的手牌列表
            for (int i = 0; i < GameState.getInstance().getPlayers().size(); i++) {
                if (i != myPlayerIndex) { // 不给自己发
                    Player clientPlayer = (Player) GameState.getInstance().getPlayers().get(i);
                    String clientDevice = bluetoothController.getDeviceByIndex(i); // 假设 BluetoothController 有根据序号获取设备的方法
                    if (clientDevice != null && clientPlayer.getHandCards() != null) {
                        bluetoothController.sendDataToClient(clientDevice, new ArrayList<>(clientPlayer.getHandCards()));
                        Log.d("MultiplayerGameFragment", "Sent initial hand cards to player " + i);
                    }
                }
            }


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
            if (GameState.getInstance() != null && myPlayerIndex < GameState.getInstance().getPlayers().size()) {
                currentPlayer = (Player) GameState.getInstance().getPlayers().get(myPlayerIndex);
                clientCurrentPlayerIndex = GameState.getInstance().getCurrentPlayerIndex(); // 房主初始化自己的当前玩家索引
            }
        }
        updateUI(); // 更新UI显示自己是房主
    }

    @Override
    public void onClientConnected(BluetoothDevice device, boolean isServer) {
//        if (!isAdded()) return;
//        if (isServer) {
//            // 当前设备是服务端（房主），有新的客户端连接
//            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "客户端 " + (device.getName() != null ? device.getName() : device.getAddress()) + " 已连接", Toast.LENGTH_SHORT).show());
//            // 房主需要为新连接的客户端分配一个序号，并告知它
//            // 【重要】房主在 MutipleController 内部管理玩家列表和序号分配
//            // 假设 MutipleController.addPlayerAndAssignIndex() 返回分配的序号
//            int assignedIndex = gameController.addPlayerAndAssignIndex(device); // 你需要在 MutipleController 中实现这个方法
//
//            if (assignedIndex != -1) {
//                bluetoothController.sendDataToClient(device, "ASSIGN_INDEX:" + assignedIndex);
//                Log.d("MultiplayerGameFragment", "Assigned index " + assignedIndex + " to " + device.getName());
//            } else {
//                Log.e("MultiplayerGameFragment", "Failed to assign index to new client: " + device.getName());
//            }

            // 房主更新UI显示连接状态和玩家数 (GameState.getInstance().getPlayers().size() 应该已经更新)
            updateUI();

//        } else {
//            // 当前设备是客户端，已成功连接到服务端
//            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "已连接到服务端 " + (device.getName() != null ? device.getName() : device.getAddress()), Toast.LENGTH_SHORT).show());
//            isHost = false;
//            // 客户端连接成功后，等待房主发送 ASSIGN_INDEX 消息来确定自己的 myPlayerIndex
//        }
    }

    @Override
    public void onClientDisconnected(BluetoothDevice device) {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), (  device.getAddress()) + " 已断开连接", Toast.LENGTH_SHORT).show());
        if (isHost) {
            // 房主更新 GameState 中的玩家列表，移除断开连接的玩家
            //gameController.removePlayer(device); // 你需要在 MutipleController 中实现这个方法
            // 广播更新后的游戏状态，包括新的玩家序号
            // 这里可以广播一个包含最新玩家列表和牌数的 TurnInfoMessage
            // 或者发送一个 GameStateUpdatedMessage (如果需要)
            updateUI(); // 更新UI显示玩家数变化
        } else {
            // 如果断开连接的是房主，客户端需要提示游戏中断或尝试重连
            new AlertDialog.Builder(requireContext())
                    .setTitle("连接断开")
                    .setMessage("房主已断开连接，游戏结束。")
                    .setPositiveButton("确定", (dialog, which) -> {
                        if (getActivity() instanceof MainActivity) {
                            // ((MainActivity) getActivity()).na(); // 返回主界面
                        }
                    })
                    .setCancelable(false)
                    .show();
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
        updateUI(); // 确保从其他 Fragment 返回时UI正确更新
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
                    handleExitGame();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void handleExitGame() {
        if (isHost) {
            if (bluetoothController != null) {
                TurnInfoMessage exitMessage = new TurnInfoMessage(
                        -3, // 约定 -3 为房主退出信号
                        new ArrayList<>(),
                        true, // 游戏结束
                        -1 // 无赢家
                );
                bluetoothController.broadcastDataToClients(exitMessage);
                bluetoothController.stopServer();
            }
            if (gameController != null) {
                gameController.quitGame(); // 调用 MutipleController 的退出方法
            }
        } else {
            if (bluetoothController != null) {
                bluetoothController.sendDataToServer("PLAYER_QUIT:" + myPlayerIndex);
                bluetoothController.disconnectClient();
            }
        }

        if (getActivity() instanceof MainActivity) { // 假设你的主 Activity 是 MainActivity
            navigateToMainPage(); // 导航回主Fragment
            Toast.makeText(getContext(), "已退出游戏。", Toast.LENGTH_SHORT).show();
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

}