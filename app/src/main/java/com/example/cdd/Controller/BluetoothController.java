package com.example.cdd.Controller;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BluetoothController {
    private BluetoothAdapter mAdapter;                 //蓝牙适配器
    private Context mContext;
    private static final int REQUEST_CODE_BLUETOOTH_DISCOVERABLE = 1001; // 用于请求可见性的请求码
    private static final int PERMISSION_REQUEST_CODE_BLUETOOTH = 1002; // 用于请求蓝牙权限的请求码
    private static final int PERMISSION_REQUEST_CODE_BLUETOOTH_SCAN = 2001; // 用于请求蓝牙扫描权限的请求码
    // 定义启动器成员变量
    private static final UUID GAME_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothListener listener; // 回调接口，通知上层状态

    private BluetoothServerSocket serverSocket; // 服务端socket
    private BluetoothSocket clientSocket;       // 客户端socket
    private InputStream inputStream;            // 输入流（接收数据）
    private OutputStream outputStream;          // 输出流（发送数据）

    private ActivityResultLauncher<Intent> requestEnableBtLauncher;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    // 用于存储发现的设备
    private ArrayList<BluetoothDevice> mFoundDevices = new ArrayList<>();
//    // 用于展示设备名称的 ArrayAdapter (可选，取决于你的UI)
//    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    // 设备发现广播接收器
    private final BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (listener != null) listener.onDeviceDiscovered(device);
            }
        }
    };

    public interface BluetoothListener {
        void onDeviceDiscovered(BluetoothDevice device); // 发现新设备
        void onServerStarted();                          // 服务端启动成功
        void onClientConnected();                        // 客户端连接成功
        void onDataReceived(String data);                // 收到数据
        void onError(String error);                      // 错误通知
    }

    public BluetoothController(){
        mAdapter = BluetoothAdapter.getDefaultAdapter();
//        initPermissionLauncher();
    }

    public BluetoothController(Context context, BluetoothListener listener){
        this.mContext = context;
        this.listener = listener;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
//        initPermissionLauncher();
    }

    /**
     * 是否支持蓝牙
     * @return ture：支持；false：不支持
     */
    public boolean isSupportBluetooth(){
        return mAdapter != null;
    }

    /**
     * 判断当前蓝牙状态
     * @return true 打开  ；false 关闭
     */
    public boolean getBluetoothStatus(){
        return mAdapter!=null&&mAdapter.isEnabled();
    }

    /**
     * 先检查，再打开
     *
     */
    public void turnOnBluetooth(){
        if (!isSupportBluetooth()){
            return;
        }
        if (!mAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            requestEnableBtLauncher.launch(enableBtIntent);
        }
    }

//    private void initPermissionLauncher() {
//        requestPermissionLauncher = ((Activity) mContext).registerForActivityResult(
//                new ActivityResultContracts.RequestMultiplePermissions(),
//                (Map<String , Boolean> permissions) -> {
//                    // 处理权限请求结果
//                    boolean allGranted = true;
//                    for (Boolean granted : permissions.values()) {
//                        if (!granted) {
//                            allGranted = false;
//                            break;
//                        }
//                    }
//                    if (allGranted) {
//                        // 权限全部授予，执行扫描等操作
//                        if (mAdapter != null) mAdapter.startDiscovery();
//                    } else {
//                        // 权限被拒绝，通知 UI
//                        if (mContext instanceof OnBluetoothPermissionListener) {
//                            ((OnBluetoothPermissionListener) mContext).onPermissionDenied();
//                        }
//                    }
//                }
//        );
//    }

    /**
     * 打开蓝牙的可见性,若权限不足，会请求开启
     * @param activity
     */
    public void enableVisibly(Activity activity){
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_ADVERTISE)
                        != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this.mContext, "蓝牙权限不足", Toast.LENGTH_SHORT).show();
            String[] permissionsNeeded;
            permissionsNeeded = new String[]{
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_ADVERTISE
            };
            activity.requestPermissions(permissionsNeeded, PERMISSION_REQUEST_CODE_BLUETOOTH);
            // 提示用户需要权限
            Toast.makeText(mContext, "请求蓝牙相关权限...", Toast.LENGTH_LONG).show();
            return;
        }
        else{
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            activity.startActivityForResult(discoverableIntent,REQUEST_CODE_BLUETOOTH_DISCOVERABLE);
        }
    }

    /**
     * 查找设备
     */
    public void findDevice(){
        if (mAdapter != null){
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this.mContext, "蓝牙权限不足", Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                mAdapter.startDiscovery();
            }
        }
    }

    // 检查并请求蓝牙权限(以下是很多蓝牙的相关权限)
    private boolean checkAndRequestPermissions() {
        if (mContext == null) return false;

        List<String> permissionsNeeded = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH_SCAN);
            }
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH);
            }
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_ADMIN)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH_ADMIN);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            requestPermissionLauncher.launch(permissionsNeeded.toArray(new String[0]));
            return false;
        }
        return true;
    }

    /**
     * 获取绑定设备
     * @return
     */
    public List<BluetoothDevice> getBondedDeviceList(){
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this.mContext, "蓝牙权限不足", Toast.LENGTH_SHORT).show();
            return Collections.emptyList();
        }
        return new ArrayList<>(mAdapter.getBondedDevices());
    }

//    /**
//     * 启动服务端，等待客户端连接
//     */
//    public void startServer() {
//        if (!checkBluetoothReady()) return; // 检查蓝牙状态和权限
//
//        new Thread(() -> {
//            try {
//                // 1. 创建服务端Socket，监听连接
//                serverSocket = mAdapter.listenUsingRfcommWithServiceRecord("GameServer", GAME_UUID);
//                if (listener != null) listener.onServerStarted();
//
//                // 2. 循环等待连接（可支持多客户端，需维护连接列表）
//                while (true) {
//                    BluetoothSocket socket = serverSocket.accept(); // 阻塞，直到有客户端连接
//                    if (socket != null) {
//                        clientSocket = socket;
//                        inputStream = socket.getInputStream();
//                        outputStream = socket.getOutputStream();
//                        if (listener != null) listener.onClientConnected();
//                        startReading(); // 开始接收数据
//                    }
//                }
//            } catch (IOException e) {
//                if (listener != null) listener.onError("服务端启动失败: " + e.getMessage());
//                closeServer();
//            }
//        }).start();
//    }
//
//    // 关闭服务端
//    private void closeServer() {
//        try {
//            if (serverSocket != null) serverSocket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    /**
//     * 客户端代码，扫描附近蓝牙设备
//     */
//    public void scanDevices() {
//        if (!checkBluetoothReady()) return;
//
//        // 注册广播接收器，监听设备发现事件
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        mContext.registerReceiver(discoveryReceiver, filter);
//
//        mAdapter.startDiscovery(); // 开始扫描
//    }
//
//    /**
//     * 连接到指定设备（作为客户端）
//     * @param device 要连接的蓝牙设备
//     */
//    public void connectToDevice(BluetoothDevice device) {
//        if (!checkBluetoothReady()) return;
//
//        new Thread(() -> {
//            try {
//                // 1. 创建客户端Socket
//                clientSocket = device.createRfcommSocketToServiceRecord(GAME_UUID);
//                // 2. 尝试连接（阻塞操作）
//                clientSocket.connect();
//                inputStream = clientSocket.getInputStream();
//                outputStream = clientSocket.getOutputStream();
//
//                if (listener != null) listener.onClientConnected();
//                startReading(); // 开始接收数据
//            } catch (IOException e) {
//                if (listener != null) listener.onError("连接失败: " + e.getMessage());
//                closeClient();
//            }
//        }).start();
//    }
//
//    // 关闭客户端连接
//    private void closeClient() {
//        try {
//            if (clientSocket != null) clientSocket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    /**
//     * 发送数据（线程安全）
//     * @param data 要发送的字符串
//     */
//    public void sendData(String data) {
//        if (outputStream == null) {
//            if (listener != null) listener.onError("未建立连接");
//            return;
//        }
//
//        new Thread(() -> {
//            try {
//                outputStream.write(data.getBytes());
//                outputStream.flush();
//            } catch (IOException e) {
//                if (listener != null) listener.onError("发送失败: " + e.getMessage());
//            }
//        }).start();
//    }

    // 持续读取数据（后台线程）
//    private void startReading() {
//        new Thread(() -> {
//            byte[] buffer = new byte[1024];
//            int bytes;
//            try {
//                while ((bytes = inputStream.read(buffer)) != -1) {
//                    String data = new String(buffer, 0, bytes);
//                    if (listener != null) listener.onDataReceived(data);
//                }
//            } catch (IOException e) {
//                if (listener != null) listener.onError("连接断开: " + e.getMessage());
//                closeClient();
//                closeServer();
//            }
//        }).start();
//    }

    public void setRequestEnableBtLauncher(ActivityResultLauncher<Intent> Launcher){
        this.requestEnableBtLauncher = Launcher;
    }
}
