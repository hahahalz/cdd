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
import java.util.List;
import java.util.UUID;

public class BluetoothController {
    private BluetoothAdapter mAdapter;                 //蓝牙适配器
    private Context mContext;
    // 定义启动器成员变量
    private static final UUID GAME_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothListener listener; // 回调接口，通知上层状态

    private BluetoothServerSocket serverSocket; // 服务端socket
    private BluetoothSocket clientSocket;       // 客户端socket
    private InputStream inputStream;            // 输入流（接收数据）
    private OutputStream outputStream;          // 输出流（发送数据）

    private ActivityResultLauncher<Intent> requestEnableBtLauncher;

    private ActivityResultLauncher<String[]> requestPermissionLauncher;

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
//                permissions -> {
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

    // 检查是否需要申请 BLUETOOTH_ADVERTISE 权限（Android 12+）
    public boolean needsAdvertisePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.BLUETOOTH_ADVERTISE)
                    != PackageManager.PERMISSION_GRANTED;
        }
        return false; // 旧版本无需该权限
    }

//    /**
//     * 打开蓝牙的可见性
//     * @param context
//     */
//    public void enableVisibly(Context context){
//        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//        context.startActivity(discoverableIntent);
//    }

//    /**
//     * 查找设备
//     */
//    public void findDevice(){
//        if (mAdapter != null){
//            try {
//                mAdapter.startDiscovery();
//            } catch (SecurityException e) {
//                e.printStackTrace();
//                Toast.makeText(this.mContext, "蓝牙权限不足", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    /**
     * 获取绑定设备
     * @return
     */
//    public List<BluetoothDevice> getBondedDeviceList(){
//        return new ArrayList<>(mAdapter.getBondedDevices());
//    }

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

    public void setRequestEnableBtLauncher(ActivityResultLauncher<Intent> Launcher){
        this.requestEnableBtLauncher = Launcher;
    }
}
