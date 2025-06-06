package com.example.cdd.View;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.widget.FrameLayout;
import androidx.appcompat.app.AlertDialog;
import android.widget.ArrayAdapter; // 新增导入
import android.widget.ListView;     // 新增导入
import android.widget.TextView;

import com.example.cdd.Controller.BluetoothController;
import com.example.cdd.Pojo.PlayerInformation;
import com.example.cdd.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BluetoothController.BluetoothListener { // 实现 BluetoothListener

    private FrameLayout fragmentContainer;

    // Initialize BluetoothController with context and listener
    private BluetoothController mBluetoothController; // 声明为成员变量

    private ActivityResultLauncher<String[]> requestPermissionLauncher; // For Bluetooth permissions
    private ActivityResultLauncher<Intent> requestEnableBtLauncher;     // For enabling Bluetooth

    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private static final int REQUEST_ADVERTISE = 100;
    private final String[] bluetoothPermissions = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE, // Added for server role
            Manifest.permission.BLUETOOTH_SCAN,      // Added for client role (discovery)
            Manifest.permission.ACCESS_FINE_LOCATION // Needed for discovery on older Android
    };

    private List<BluetoothDevice> discoveredDevices = new ArrayList<>();
    private ArrayAdapter<String> deviceListAdapter;
    private AlertDialog discoveryDialog;
    //private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mBluetoothController = new BluetoothController(this, this); // 初始化 BluetoothController，传入 Context 和 BluetoothListener

        // 初始化 Launchers
        requestEnableBtLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(this, "蓝牙已启用", Toast.LENGTH_SHORT).show();
                        // 蓝牙启用成功后，根据之前的操作意图执行
                        // 如果是创建房间，则启动服务端
                        // 如果是加入房间，则开始发现设备
                        // 这里可以根据一个临时变量来判断是从哪个操作过来的
                    } else {
                        Toast.makeText(this, "蓝牙未启用，无法进行相关操作", Toast.LENGTH_SHORT).show();
                    }
                });
        mBluetoothController.setRequestEnableBtLauncher(requestEnableBtLauncher);

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    boolean allGranted = true;
                    for (Boolean granted : permissions.values()) {
                        if (!granted) {
                            allGranted = false;
                            break;
                        }
                    }
                    if (allGranted) {
                        Toast.makeText(this, "蓝牙权限已授予", Toast.LENGTH_SHORT).show();
                        // 权限授予后，根据之前的操作意图执行
                        // 例如，如果之前是点击了创建房间，现在就可以启动服务端
                        // 如果是加入房间，现在就可以开始发现设备
                    } else {
                        Toast.makeText(this, "需要蓝牙权限才能进行多人游戏操作", Toast.LENGTH_SHORT).show();
                    }
                });
        mBluetoothController.setRequestPermissionLauncher(requestPermissionLauncher);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button button_login = findViewById(R.id.button_login);
        button_login.setOnClickListener(this);
        Button button_multi = findViewById(R.id.button_multi);
        button_multi.setOnClickListener(this);
        Button button_single = findViewById(R.id.button_single);
        button_single.setOnClickListener(this);
        Button button_top = findViewById(R.id.button_top);
        button_top.setOnClickListener(this);

        button_multi.setOnClickListener(v -> showMultiplayerDialog()); // 修改为新的方法

        TextView loginStatus = findViewById(R.id.login_status);

        String userid = PlayerInformation.getThePlayerInformation().getUserID();

        if (userid != null && !userid.isEmpty()) {
            loginStatus.setText("您好：" + userid);
        } else {
            loginStatus.setText("未登录");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLoginStatus();
    }

    private void updateLoginStatus() {
        TextView loginStatus = findViewById(R.id.login_status);
        String userid = PlayerInformation.getThePlayerInformation().getUserID();

        if (userid != null && !userid.isEmpty()) {
            loginStatus.setText("您好：" + userid);
        } else {
            loginStatus.setText("未登录");
        }
    }

    // 新的 showMultiplayerDialog 方法
    private void showMultiplayerDialog() {
        new AlertDialog.Builder(this)
                .setTitle("多人游戏")
                .setMessage("请选择您要进行的操作")
                .setPositiveButton("加入房间", (dialog, which) -> {
                    // 客户端逻辑：开始发现设备
                    if (!mBluetoothController.getBluetoothStatus()) {
                        Toast.makeText(this, "请先开启蓝牙", Toast.LENGTH_SHORT).show();
                        // 尝试开启蓝牙
                        mBluetoothController.turnOnBluetooth();
                        return;
                    }
                    mBluetoothController.findDevice(); // 开始发现设备
                    Toast.makeText(this, "正在扫描可用房间...", Toast.LENGTH_SHORT).show();
                    showDiscoveryDialog(); // 显示扫描对话框
                })
                .setNeutralButton("使当前设备蓝牙可见", (dialog, which) -> {
                    // 请求蓝牙可见性
                    if (!mBluetoothController.getBluetoothStatus()) {
                        Toast.makeText(this, "请先开启蓝牙", Toast.LENGTH_SHORT).show();
                        mBluetoothController.turnOnBluetooth();
                        return;
                    }
                    mBluetoothController.enableVisibly(this);
                    Toast.makeText(this, "已请求开启蓝牙可见性！", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("创建房间", (dialog, which) -> {
                    // 服务器逻辑：启动服务器
                    if (!mBluetoothController.getBluetoothStatus()) {
                        Toast.makeText(this, "请先开启蓝牙", Toast.LENGTH_SHORT).show();
                        mBluetoothController.turnOnBluetooth();
                        return;
                    }
                    mBluetoothController.startServer(); // 启动服务端
                    Toast.makeText(this, "正在创建房间，等待其他设备连接...", Toast.LENGTH_LONG).show();
                    // 这里可以跳转到一个等待界面，显示已连接的客户端列表
                    replaceFragement(new MultiplayerGameFragment()); // 示例：直接跳转到游戏界面
                })
                .setCancelable(true) // 允许点击外部关闭，或者根据需求设置为 false
                .show();
    }

    private void showDiscoveryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("发现蓝牙设备");

        discoveredDevices.clear();
        deviceListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        ListView listView = new ListView(this);
        listView.setAdapter(deviceListAdapter);
        builder.setView(listView);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            BluetoothDevice selectedDevice = discoveredDevices.get(position);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "蓝牙权限不足", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(MainActivity.this, "尝试连接到: " + selectedDevice.getName(), Toast.LENGTH_SHORT).show();
            mBluetoothController.connectToDevice(selectedDevice); // 连接到选择的设备
            discoveryDialog.dismiss(); // 关闭发现对话框
        });

        builder.setNegativeButton("取消", (dialog, which) -> {
            mBluetoothController.cancelDiscovery(); // 取消发现
            dialog.dismiss();
        });

        discoveryDialog = builder.create();
        discoveryDialog.show();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_login){
            replaceFragement(new LoginFragment());
        }
        else if (view.getId() == R.id.button_multi){
            // 原有的 handleBluetoothAndStartMultiplayer() 逻辑现在被 showMultiplayerDialog() 接管
            showMultiplayerDialog();
        }
        else if (view.getId() == R.id.button_single){
            replaceFragement(new RulesFragment());
        }
        else if(view.getId() == R.id.button_top){
            replaceFragement(new TopFragment());
        }
    }

    // 移除 handleBluetoothAndStartMultiplayer，其逻辑已整合到 showMultiplayerDialog
    // @Override
    // public void onRequestPermissionsResult(...) { ... } // 权限结果处理现在由 requestPermissionLauncher 处理

    public void showFragment(Fragment fragment) {
        fragmentContainer.setClickable(true);
        fragmentContainer.setVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.framelayout, fragment)
                .commit();
    }

    public void hideFragment() {
        fragmentContainer.setClickable(false);
        fragmentContainer.setVisibility(View.INVISIBLE);

        getSupportFragmentManager().beginTransaction()
                .remove(getSupportFragmentManager().findFragmentById(R.id.framelayout))
                .commit();
    }

    public void replaceFragement(Fragment fragment) {
        if (fragmentContainer == null) {
            fragmentContainer = findViewById(R.id.framelayout);
        }
        if (fragmentContainer != null && fragment != null) {
            fragmentContainer.setClickable(true);
            fragmentContainer.setVisibility(View.VISIBLE);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.framelayout, fragment);
            transaction.commit();
        }
    }

    public void sendMessageToSingleplayerGameFragment(String message, SingleplayerGameFragment fragment) {
        if (fragment != null) {
            fragment.receiveMessage(message);
        }
    }

    public void sendMessageToDifficultyFragment(String message, DifficultyFragment fragment) {
        if (fragment != null) {
            fragment.receiveMessage(message);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothController.onDestroy(); // 清理 BluetoothController 资源
    }

    // ---------------------------------------------------------------------------------------------
    // BluetoothController.BluetoothListener 实现
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onDeviceDiscovered(BluetoothDevice device) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "蓝牙权限不足", Toast.LENGTH_SHORT).show();
            return;
        }
        if (device != null  && !discoveredDevices.contains(device)) {
            discoveredDevices.add(device);
            deviceListAdapter.add(device.getName() + "\n" + device.getAddress());
            deviceListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDiscoveryFinished(List<BluetoothDevice> devices) {
        Toast.makeText(this, "扫描结束，发现 " + devices.size() + " 个设备", Toast.LENGTH_SHORT).show();
        // 扫描结束时，如果发现对话框还在，可以更新UI或自动关闭
        if (discoveryDialog != null && discoveryDialog.isShowing()) {
            if (devices.isEmpty()) {
                Toast.makeText(this, "未发现任何蓝牙设备。", Toast.LENGTH_LONG).show();
                // 可以选择自动关闭对话框
                // discoveryDialog.dismiss();
            }
        }
    }

    @Override
    public void onServerStarted() {
        Toast.makeText(this, "服务端已启动，等待客户端连接...", Toast.LENGTH_LONG).show();
        // 可以在这里更新UI，例如显示“等待连接中...”
    }

    @Override
    public void onClientConnected(BluetoothDevice device, boolean isServer) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "蓝牙权限不足", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isServer) {
            Toast.makeText(this, "新客户端连接: " + (device.getName() != null ? device.getName() : device.getAddress()), Toast.LENGTH_LONG).show();
            // 作为服务器，有新客户端连接，更新已连接客户端列表UI
        } else {
            Toast.makeText(this, "已成功连接到服务端: " + (device.getName() != null ? device.getName() : device.getAddress()), Toast.LENGTH_LONG).show();
            // 作为客户端，连接到服务端，可以跳转到游戏界面或发送数据
            replaceFragement(new MultiplayerGameFragment());
            // 此时可以发送数据
            mBluetoothController.sendDataToServer("Hello from client!");
        }
    }

    @Override
    public void onDataReceived(BluetoothDevice fromDevice, String data) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "蓝牙权限不足", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "收到来自 " + (fromDevice.getName() != null ? fromDevice.getName() : fromDevice.getAddress()) + " 的数据: " + data, Toast.LENGTH_SHORT).show();
        // 处理接收到的数据，例如更新游戏状态
    }

    @Override
    public void onClientDisconnected(BluetoothDevice device) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "蓝牙权限不足", Toast.LENGTH_SHORT).show();
            return;
        }
        String deviceName = (device != null && device.getName() != null) ? device.getName() : "未知设备";
        Toast.makeText(this, deviceName + " 已断开连接", Toast.LENGTH_SHORT).show();
        // 更新UI，例如从已连接客户端列表中移除
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, "蓝牙错误: " + error, Toast.LENGTH_LONG).show();
        // 处理错误，例如显示错误信息给用户
    }
}