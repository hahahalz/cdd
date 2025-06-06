package com.example.cdd.View;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cdd.Controller.BluetoothController;
import com.example.cdd.Controller.GameController;
import com.example.cdd.Model.Card;
import com.example.cdd.Model.GameState;
import com.example.cdd.Model.Player;
import com.example.cdd.Pojo.PlayerInformation;
import com.example.cdd.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable; // 导入 Serializable
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MultiplayerGameFragment extends Fragment implements BluetoothController.BluetoothListener { // 修改接口名称

    private BluetoothController bluetoothController;
    private GameController gameController;

    private TextView tvPlayer1Name, tvPlayer2Name, tvPlayer3Name, tvPlayer4Name;
    private TextView tvPlayer1CardsCount, tvPlayer2CardsCount, tvPlayer3CardsCount, tvPlayer4CardsCount;
    private LinearLayout llPlayer1Hand, llPlayer1Played, llPlayer2Played, llPlayer3Played, llPlayer4Played;
    private Button btnPlayCards, btnPass, btnReady;

    private List<Card> selectedCards = new ArrayList<>();
    private List<ImageView> player1HandCardImageViews = new ArrayList<>();

    private Player currentPlayer; // 当前玩家，即本地玩家

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // 获取 BluetoothController 实例
        if (context instanceof MainActivity) {
            bluetoothController = ((MainActivity) context).getBluetoothController();
            if (bluetoothController != null) {
                bluetoothController.setListener(this);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multiplayer_game, container, false);

        initViews(view);
        setupListeners();
        gameController = GameController.getInstance(); // 获取单例

        // 初始化当前玩家信息（假设本地玩家是PlayerInformation.getThePlayerInformation()）
        // 在实际多人游戏中，需要根据连接的蓝牙设备确定玩家顺序和分配Player对象
        currentPlayer = new Player(PlayerInformation.getThePlayerInformation());
        gameController.addPlayer(currentPlayer); // 将本地玩家加入游戏控制器

        updateUI(); // 初始UI更新

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
    }

    private void setupListeners() {
        btnPlayCards.setOnClickListener(v -> playSelectedCards());
        btnPass.setOnClickListener(v -> passTurn());
        btnReady.setOnClickListener(v -> sendReadySignal());
    }

    private void sendReadySignal() {
        // 通知其他玩家本地玩家已准备
        String readyMessage = "READY:" + currentPlayer.getPlayerInformation().getUserID();
        if (bluetoothController != null) {
            // write 方法现在接受 Serializable 类型
            bluetoothController.sendDataToServer((Serializable) readyMessage); //
            Toast.makeText(getContext(), "已发送准备信号", Toast.LENGTH_SHORT).show();
            btnReady.setEnabled(false); // 准备后禁用按钮
        }
    }

    private void playSelectedCards() {
        if (selectedCards.isEmpty()) {
            Toast.makeText(getContext(), "请选择要出的牌", Toast.LENGTH_SHORT).show();
            return;
        }

        GameState gameState = GameState.getInstance();
        if (gameState == null) {
            Toast.makeText(getContext(), "游戏状态未初始化", Toast.LENGTH_SHORT).show();
            return;
        }

        // 检查是否是当前玩家的回合
        if (gameState.getPlayers().get(gameState.getCurrentPlayerIndex()) != currentPlayer) {
            Toast.makeText(getContext(), "还没轮到你出牌", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Card> cardsToPlay = new ArrayList<>(selectedCards); // 复制一份，避免ConcurrentModificationException

        boolean isValidMove = gameController.playCards(currentPlayer, cardsToPlay); // 使用GameController判断并出牌

        if (isValidMove) {
            Toast.makeText(getContext(), "出牌成功！", Toast.LENGTH_SHORT).show();
            selectedCards.clear(); // 清空已选择的牌
            updateUI();
            // 发送游戏状态更新给其他玩家
            sendGameStateToAllPlayers();
        } else {
            Toast.makeText(getContext(), "出牌不符合规则，请重新选择", Toast.LENGTH_SHORT).show();
        }
    }

    private void passTurn() {
        GameState gameState = GameState.getInstance();
        if (gameState == null) {
            Toast.makeText(getContext(), "游戏状态未初始化", Toast.LENGTH_SHORT).show();
            return;
        }

        // 检查是否是当前玩家的回合
        if (gameState.getPlayers().get(gameState.getCurrentPlayerIndex()) != currentPlayer) {
            Toast.makeText(getContext(), "还没轮到你，无法过牌", Toast.LENGTH_SHORT).show();
            return;
        }

        currentPlayer.pass(); // 玩家过牌
        Toast.makeText(getContext(), "已过牌", Toast.LENGTH_SHORT).show();
        updateUI();
        sendGameStateToAllPlayers(); // 发送游戏状态更新
    }

    private void updateUI() {
        if (!isAdded() || getContext() == null) {
            return; // Fragment not attached or context not available
        }
        requireActivity().runOnUiThread(() -> {
            GameState gameState = GameState.getInstance();
            if (gameState == null) {
                Log.e("MultiplayerGameFragment", "GameState is null, cannot update UI.");
                return;
            }

            // 更新玩家1（本地玩家）手牌
            llPlayer1Hand.removeAllViews();
            player1HandCardImageViews.clear();
            if (currentPlayer != null && currentPlayer.getHandCards() != null) {
                for (Card card : currentPlayer.getHandCards()) {
                    ImageView cardImage = createCardImageView(card);
                    cardImage.setOnClickListener(v -> toggleCardSelection(card, cardImage));
                    llPlayer1Hand.addView(cardImage);
                    player1HandCardImageViews.add(cardImage);
                }
                tvPlayer1CardsCount.setText("手牌: " + currentPlayer.getHandCards().size());
                tvPlayer1Name.setText(currentPlayer.getPlayerInformation().getUserID());
            }

            // 更新场上已出的牌
            llPlayer1Played.removeAllViews(); // 清空之前出过的牌
            llPlayer2Played.removeAllViews();
            llPlayer3Played.removeAllViews();
            llPlayer4Played.removeAllViews();

            List<Card> lastPlayed = gameState.getLastPlayedCards();
            if (lastPlayed != null && !lastPlayed.isEmpty()) {
                // 假设最后一个出牌的玩家是 currentPlayerIndex 的前一个玩家（如果不是第一个玩家）
                // 实际需要根据游戏逻辑判断是哪个玩家出的牌
                // 这里简化处理，直接显示所有在桌上的牌
                for (Card card : lastPlayed) {
                    ImageView cardImage = createCardImageView(card);
                    // 根据实际逻辑判断是哪个玩家出的牌，然后添加到对应的ll_playerX_played
                    // 暂时都放到玩家1的已出牌区域，实际需要更复杂的逻辑来区分
                    llPlayer1Played.addView(cardImage);
                }
            }


            // 更新其他玩家信息 (简单显示牌数和名称，实际需要根据蓝牙连接的设备来更新)
            List<Player> remotePlayers = new ArrayList<>();
            for (int i = 0; i < gameState.getPlayers().size(); i++) {
                if (gameState.getPlayers().get(i) != currentPlayer) {
                    remotePlayers.add((Player) gameState.getPlayers().get(i));
                }
            }

            // 假设只有2个玩家，简化处理
            if (remotePlayers.size() >= 1) {
                Player p2 = remotePlayers.get(0);
                tvPlayer2Name.setText(p2.getPlayerInformation().getUserID());
                tvPlayer2CardsCount.setText("手牌: " + p2.getHandCards().size());
            } else {
                tvPlayer2Name.setText("玩家2 (未连接)");
                tvPlayer2CardsCount.setText("手牌: 0");
            }
            if (remotePlayers.size() >= 2) {
                Player p3 = remotePlayers.get(1);
                tvPlayer3Name.setText(p3.getPlayerInformation().getUserID());
                tvPlayer3CardsCount.setText("手牌: 0");
            } else {
                tvPlayer3Name.setText("玩家3 (未连接)");
                tvPlayer3CardsCount.setText("手牌: 0");
            }
            if (remotePlayers.size() >= 3) {
                Player p4 = remotePlayers.get(2);
                tvPlayer4Name.setText(p4.getPlayerInformation().getUserID());
                tvPlayer4CardsCount.setText("手牌: " + p4.getHandCards().size());
            } else {
                tvPlayer4Name.setText("玩家4 (未连接)");
                tvPlayer4CardsCount.setText("手牌: 0");
            }


            // 检查游戏是否结束
            if (gameState.isGameOver()) {
                String winnerName = (gameState.getWinner() != null) ?
                        ((Player) gameState.getWinner()).getPlayerInformation().getUserID() : "未知";
                showGameEndDialog(winnerName);
                // 游戏结束，禁用出牌和过牌按钮
                btnPlayCards.setEnabled(false);
                btnPass.setEnabled(false);
                btnReady.setEnabled(true); // 游戏结束后允许重新准备
            } else {
                // 根据当前轮到谁出牌来启用/禁用按钮
                if (gameState.getPlayers().get(gameState.getCurrentPlayerIndex()) == currentPlayer) {
                    btnPlayCards.setEnabled(true);
                    btnPass.setEnabled(true);
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
        if (resId != 0) {
            imageView.setImageResource(resId);
        } else {
            imageView.setImageResource(R.drawable.card_back); // 默认显示牌背
            Log.e("MultiplayerGameFragment", "Card resource not found: " + cardResourceName);
        }
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
        if (!isAdded()) return; // Fragment not attached
        new AlertDialog.Builder(requireContext())
                .setTitle("游戏结束")
                .setMessage(winnerName + " 赢得了本轮游戏！")
                .setPositiveButton("确定", (dialog, which) -> {
                    // 可以选择在这里开始新一轮游戏或返回主菜单
                    gameController.resetGame(); // 重置游戏状态
                    updateUI(); // 更新UI
                })
                .setCancelable(false)
                .show();
    }


    private void sendGameStateToAllPlayers() {
        if (bluetoothController != null && getContext() != null) {
            // write 方法现在接受 Serializable 类型
            bluetoothController.sendDataToServer(GameState.getInstance()); //
            Log.d("MultiplayerGameFragment", "Game State sent.");
        }
    }

    @Override
    public void onDataReceived(BluetoothDevice fromDevice, Object data) { // 更改参数类型为 Object
        // 在此处检查权限，以防在设备名称获取时权限状态发生变化
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "蓝牙连接权限不足，无法显示发送方设备名称", Toast.LENGTH_SHORT).show());
            return;
        }

        String deviceName = (fromDevice != null && fromDevice.getName() != null) ? fromDevice.getName() : fromDevice.getAddress();

        if (data instanceof GameState) { // 直接判断接收到的对象类型
            GameState receivedGameState = (GameState) data;
            Log.d("MultiplayerGameFragment", "Received GameState from " + deviceName);
            // 更新本地的 GameState
            GameState.setInstance(receivedGameState); // 更新单例

            // 检查是否是READY信号 (这部分逻辑可能需要重新考虑，因为READY信号现在可能直接是字符串)
            if (receivedGameState.getPlayers() != null) {
                for (int i = 0; i < receivedGameState.getPlayers().size(); i++) {
                    if (receivedGameState.getPlayers().get(i) instanceof Player) {
                        Player p = (Player) receivedGameState.getPlayers().get(i);
                        // 假设我们通过PlayerInformation的UserID来识别玩家
                        if (p.getPlayerInformation().getUserID().equals(deviceName)) { // 简单的匹配，实际需要更可靠的ID
                            // 处理准备信号，例如更新UI显示玩家已准备
                            Log.d("MultiplayerGameFragment", deviceName + " is READY.");
                            // 可以在这里更新UI，例如在玩家名称旁边显示“已准备”
                        }
                    }
                }
            }
            updateUI(); // 收到新状态后更新UI
            Toast.makeText(getContext(), "收到来自 " + deviceName + " 的游戏状态更新", Toast.LENGTH_SHORT).show();

        } else if (data instanceof String) { // 直接判断接收到的对象类型
            String message = (String) data;
            if (message.startsWith("READY:")) {
                String senderId = message.substring(6);
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), senderId + " 已准备", Toast.LENGTH_SHORT).show());
                Log.d("MultiplayerGameFragment", senderId + " sent READY signal.");
                // 如果所有玩家都准备好了，可以开始游戏
                checkAllPlayersReady();
            } else {
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "收到来自 " + deviceName + " 的数据: " + message, Toast.LENGTH_SHORT).show());
            }
        } else {
            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "收到来自 " + deviceName + " 的未知数据", Toast.LENGTH_SHORT).show());
            Log.w("MultiplayerGameFragment", "Received unknown object type from " + deviceName);
        }
    }


    private void checkAllPlayersReady() {
        // 简单示例：假设有两名玩家，一个本地玩家和一个远程玩家
        // 在实际应用中，需要维护一个所有连接玩家的“准备”状态列表
        // 这里只是一个占位符，需要更复杂的逻辑
        // 注意：getConnectedClients() 返回的是 Map<String, ConnectedThread>，不能直接判断玩家数量
        // 你需要通过其他方式来追踪连接的玩家数量和他们的准备状态
        // 这里暂时简化为：只要有一个连接，就认为可以尝试开始游戏
        if (bluetoothController != null && !bluetoothController.getConnectedClients().isEmpty()) { // 使用 isEmpty() 检查是否有连接
            Log.d("MultiplayerGameFragment", "All players seem ready, starting game...");
            startGame();
        }
    }


    private void startGame() {
        // 游戏开始逻辑，发牌，设置初始玩家等
        if (gameController != null) {
            gameController.startGame();
            updateUI();
            sendGameStateToAllPlayers(); // 游戏开始时发送初始状态
            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "游戏开始！", Toast.LENGTH_LONG).show());
        }
    }

    @Override
    public void onDeviceDiscovered(BluetoothDevice device) { // 方法名称更改
        // 不在这里处理，由 MainActivity 处理
    }

    @Override
    public void onDiscoveryFinished(List<BluetoothDevice> devices) { // 方法签名更改
        // 不在这里处理，由 MainActivity 处理
    }

    @Override
    public void onServerStarted() { // 新增服务端启动回调
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "服务端已启动，等待连接...", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onClientConnected(BluetoothDevice device, boolean isServer) { // 客户端连接回调
        if (!isAdded()) return;
        if (isServer) {
            // 当前设备是服务端，有新的客户端连接
            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "客户端 " + (device.getName() != null ? device.getName() : device.getAddress()) + " 已连接", Toast.LENGTH_SHORT).show());
            // 服务端接收到新连接时，可以发送当前游戏状态或者请求客户端的玩家信息
            sendGameStateToAllPlayers(); // 服务端连接新客户端时，广播最新游戏状态
        } else {
            // 当前设备是客户端，已成功连接到服务端
            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "已连接到服务端 " + (device.getName() != null ? device.getName() : device.getAddress()), Toast.LENGTH_SHORT).show());
            // 如果连接成功，可以考虑发送本地玩家信息或者请求完整的游戏状态
            sendPlayerInformation();
        }
    }

    @Override
    public void onClientDisconnected(BluetoothDevice device) { // 方法名称更改
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), (device.getName() != null ? device.getName() : device.getAddress()) + " 已断开连接", Toast.LENGTH_SHORT).show());
        // 处理断开连接的情况，例如更新玩家列表
    }

    @Override
    public void onError(String message) { // 错误回调
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "蓝牙错误: " + message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onLog(String message) { // 新增日志回调
        Log.d("BluetoothController_Log", message);
        // 如果需要，可以在UI上显示这些日志信息
    }


    private void sendPlayerInformation() {
        if (bluetoothController != null && currentPlayer != null) {
            // sendDataToServer 方法现在接受 Serializable 类型
            bluetoothController.sendDataToServer(currentPlayer.getPlayerInformation()); // 发送玩家信息
            Log.d("MultiplayerGameFragment", "Player information sent. UserID: " + currentPlayer.getPlayerInformation().getUserID());
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (bluetoothController != null) {
            bluetoothController.setListener(this); // 确保 Fragment 处于活跃状态时监听器已设置
        }
        updateUI(); // 确保从其他 Fragment 返回时UI正确更新
    }

    @Override
    public void onPause() {
        super.onPause();
        // 可以在这里移除监听器，如果蓝牙通信需要在后台持续进行则不移除
        // if (bluetoothController != null) {
        //     bluetoothController.setListener(null);
        // }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        selectedCards.clear();
        player1HandCardImageViews.clear();
        // 清理UI元素引用，避免内存泄漏
    }
}