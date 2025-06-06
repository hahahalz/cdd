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
import android.content.IntentFilter; // 新增
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BluetoothController {
    private BluetoothAdapter mAdapter;                 // 蓝牙适配器
    private Context mContext;
    private static final int REQUEST_CODE_BLUETOOTH_DISCOVERABLE = 1001; // 用于请求可见性的请求码
    private static final int PERMISSION_REQUEST_CODE_BLUETOOTH = 1002; // 用于请求蓝牙权限的请求码
    private static final int PERMISSION_REQUEST_CODE_BLUETOOTH_SCAN = 2001; // 用于请求蓝牙扫描权限的请求码
    // 定义启动器成员变量
    private static final UUID GAME_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothListener listener; // 回调接口，通知上层状态

    private AcceptThread acceptThread; // 服务端监听线程
    private ConnectedThread clientConnectedThread; // 客户端连接后的通信线程 (如果只连接一个服务器)
    private Map<String, ConnectedThread> connectedClients; // 服务端管理多个客户端的通信线程

    private BluetoothServerSocket serverSocket; // 服务端socket
    private BluetoothSocket clientSocket;       // 客户端socket (对于客户端模式)
    // InputStream 和 OutputStream 不再是直接成员变量，而是由 ConnectedThread 管理

    private ActivityResultLauncher<Intent> requestEnableBtLauncher;
    private ActivityResultLauncher<String[]> requestPermissionLauncher; // 这是需要从外部传入的

    // 用于存储发现的设备
    private ArrayList<BluetoothDevice> mFoundDevices = new ArrayList<>();

    // 设备发现广播接收器
    private final BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && !mFoundDevices.contains(device)) { // 避免重复添加
                    mFoundDevices.add(device);
                    if (listener != null) listener.onDeviceDiscovered(device);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // 扫描结束
                if (listener != null) listener.onDiscoveryFinished(mFoundDevices); // 新增回调
                // 注意：这里取消注册的逻辑可能需要在外部处理，因为BluetoothController不一定知道Activity的生命周期
                // mContext.unregisterReceiver(this); // 如果在Activity/Fragment中注册，应在那里unregister
            }
        }
    };

    // 用于管理所有后台通信线程的线程池
    private ExecutorService executorService = Executors.newCachedThreadPool(); // 新增

    public interface BluetoothListener {
        void onDeviceDiscovered(BluetoothDevice device); // 发现新设备
        void onDiscoveryFinished(List<BluetoothDevice> devices); // 扫描结束，返回所有发现的设备
        void onServerStarted();                          // 服务端启动成功
        void onClientConnected(BluetoothDevice device, boolean isServer); // 客户端连接成功 (isServer表示当前是服务端，有新客户端连接)
        void onDataReceived(BluetoothDevice fromDevice, String data); // 收到数据，指明来源设备
        void onClientDisconnected(BluetoothDevice device); // 客户端断开连接
        void onError(String error);                      // 错误通知
    }

    public BluetoothController(){
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        connectedClients = new HashMap<>(); // 初始化
    }

    public BluetoothController(Context context, BluetoothListener listener){
        this.mContext = context;
        this.listener = listener;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        connectedClients = new HashMap<>(); // 初始化
    }

    /**
     * 是否支持蓝牙
     * @return true：支持；false：不支持
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
     */
    public void turnOnBluetooth(){
        if (!isSupportBluetooth()){
            Toast.makeText(mContext, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mAdapter.isEnabled()) {
            if (requestEnableBtLauncher != null) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                requestEnableBtLauncher.launch(enableBtIntent);
            } else {
                Toast.makeText(mContext, "请在Activity/Fragment中设置ActivityResultLauncher来处理蓝牙开启请求", Toast.LENGTH_LONG).show();
            }
        }
    }

    // 移除 initPermissionLauncher 方法，它应该在 Activity/Fragment 中处理

    /**
     * 打开蓝牙的可见性,若权限不足，会请求开启
     * @param activity 用于请求权限的Activity实例（现在可以只用mContext来判断，但requestPermissionLauncher需要宿主的上下文）
     */
    public void enableVisibly(Activity activity){ // 保持Activity参数是为了兼容旧的startActivityForResult
        if (mContext == null) {
            Toast.makeText(activity, "BluetoothController 未初始化 Context", Toast.LENGTH_SHORT).show();
            return;
        }
        List<String> permissionsNeeded = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH_ADVERTISE);
            }
        } else {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH_ADMIN);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            if (requestPermissionLauncher != null) {
                requestPermissionLauncher.launch(permissionsNeeded.toArray(new String[0]));
                Toast.makeText(mContext, "请求蓝牙可见性相关权限...", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mContext, "请在Activity/Fragment中设置ActivityResultLauncher来处理权限请求", Toast.LENGTH_LONG).show();
            }
            return;
        }
        else{
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            activity.startActivityForResult(discoverableIntent,REQUEST_CODE_BLUETOOTH_DISCOVERABLE); // 这个依然依赖老API
        }
    }

    /**
     * 查找设备
     */
    public void findDevice(){
        if (mAdapter == null){
            Toast.makeText(mContext, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mAdapter.isEnabled()){
            Toast.makeText(mContext, "请先开启蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }

        // 检查蓝牙扫描权限
        boolean hasPermission = false;
        List<String> permissionsToRequest = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                hasPermission = true;
            } else {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN);
            }
        } else { // Android 11 及以下版本，只需要 BLUETOOTH 和 BLUETOOTH_ADMIN 以及位置权限
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) { // 扫描需要位置权限
                hasPermission = true;
            } else {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(Manifest.permission.BLUETOOTH);
                }
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(Manifest.permission.BLUETOOTH_ADMIN);
                }
                // Android 11 及以下蓝牙扫描需要位置权限
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
                }
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            if (requestPermissionLauncher != null) {
                requestPermissionLauncher.launch(permissionsToRequest.toArray(new String[0]));
                Toast.makeText(mContext, "请求蓝牙扫描权限...", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mContext, "请在Activity/Fragment中设置ActivityResultLauncher来处理权限请求", Toast.LENGTH_LONG).show();
            }
            return;
        }

        if (hasPermission) {
            if (mAdapter.isDiscovering()) {
                mAdapter.cancelDiscovery(); // 如果正在扫描，先取消
            }
            mFoundDevices.clear(); // 清空上次扫描结果
            // 注册广播接收器。这里需要外部来处理 unregisterReceiver，或者确保在 onDestroy 中调用
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); // 监听扫描结束
            mContext.registerReceiver(discoveryReceiver, filter); // 这里是注册
            Toast.makeText(mContext, "开始扫描蓝牙设备...", Toast.LENGTH_SHORT).show();
            mAdapter.startDiscovery(); // 开始扫描
        }
    }

    /**
     * 获取绑定设备
     */
    public List<BluetoothDevice> getBondedDeviceList(){
        if (mAdapter == null || !mAdapter.isEnabled()) {
            Toast.makeText(this.mContext, "蓝牙未开启或不支持", Toast.LENGTH_SHORT).show();
            return Collections.emptyList();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this.mContext, "蓝牙连接权限不足", Toast.LENGTH_SHORT).show();
                // 可以在这里请求权限，但最好在调用前确保权限已授予
                return Collections.emptyList();
            }
        } else {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this.mContext, "蓝牙权限不足", Toast.LENGTH_SHORT).show();
                return Collections.emptyList();
            }
        }
        return new ArrayList<>(mAdapter.getBondedDevices());
    }

    /**
     * 启动服务端，等待客户端连接（支持多客户端）
     */
    public void startServer() {
        if (!getBluetoothStatus()) {
            if (listener != null) listener.onError("蓝牙未开启，无法启动服务端");
            return;
        }

        // 确保权限已授予
        List<String> permissionsNeededForServer = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeededForServer.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeededForServer.add(Manifest.permission.BLUETOOTH_ADVERTISE);
            }
        } else {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeededForServer.add(Manifest.permission.BLUETOOTH);
            }
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeededForServer.add(Manifest.permission.BLUETOOTH_ADMIN);
            }
        }

        if (!permissionsNeededForServer.isEmpty()) {
            if (requestPermissionLauncher != null) {
                requestPermissionLauncher.launch(permissionsNeededForServer.toArray(new String[0]));
                Toast.makeText(mContext, "启动服务端需要蓝牙连接和广告权限，请求中...", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mContext, "请在Activity/Fragment中设置ActivityResultLauncher来处理权限请求", Toast.LENGTH_LONG).show();
            }
            return;
        }

        if (acceptThread != null) {
            if (listener != null) listener.onError("服务端已在运行");
            return;
        }

        acceptThread = new AcceptThread();
        executorService.execute(acceptThread); // 使用线程池执行
    }

    /**
     * 停止服务端
     */
    public void stopServer() {
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
        // 关闭所有已连接的客户端
        for (ConnectedThread clientThread : connectedClients.values()) {
            clientThread.cancel();
        }
        connectedClients.clear();
        Toast.makeText(mContext, "服务端已停止", Toast.LENGTH_SHORT).show();
    }

    /**
     * 连接到指定设备（作为客户端）
     * @param device 要连接的蓝牙设备
     */
    public void connectToDevice(BluetoothDevice device) {
        if (!getBluetoothStatus()) {
            if (listener != null) listener.onError("蓝牙未开启，无法连接设备");
            return;
        }
        List<String> permissionsNeededForClient = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeededForClient.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
        } else {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeededForClient.add(Manifest.permission.BLUETOOTH);
            }
        }

        if (!permissionsNeededForClient.isEmpty()) {
            if (requestPermissionLauncher != null) {
                requestPermissionLauncher.launch(permissionsNeededForClient.toArray(new String[0]));
                Toast.makeText(mContext, "连接设备需要蓝牙连接权限，请求中...", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mContext, "请在Activity/Fragment中设置ActivityResultLauncher来处理权限请求", Toast.LENGTH_LONG).show();
            }
            return;
        }


        // 如果正在扫描，先取消，因为扫描会减慢连接速度
        if (mAdapter.isDiscovering()) {
            mAdapter.cancelDiscovery();
        }

        // 如果之前有客户端连接线程，先取消
        if (clientConnectedThread != null) {
            clientConnectedThread.cancel();
            clientConnectedThread = null;
        }

        ConnectThread connectThread = new ConnectThread(device);
        executorService.execute(connectThread); // 使用线程池执行
    }

    /**
     * 断开客户端连接 (当前设备作为客户端时的连接)
     */
    public void disconnectClient() {
        if (clientConnectedThread != null) {
            clientConnectedThread.cancel();
            clientConnectedThread = null;
            if (listener != null) listener.onClientDisconnected(null); // 通知客户端已断开
            Toast.makeText(mContext, "客户端已断开连接", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 作为服务端，向特定客户端发送数据
     * @param deviceAddress 客户端设备的MAC地址
     * @param data 要发送的字符串
     */
    public void sendDataToClient(String deviceAddress, String data) {
        ConnectedThread clientThread = connectedClients.get(deviceAddress);
        if (clientThread != null) {
            clientThread.write(data.getBytes());
        } else {
            if (listener != null) listener.onError("客户端 " + deviceAddress + " 未连接或已断开");
        }
    }

    /**
     * 作为服务端，向所有连接的客户端广播数据
     * @param data 要广播的字符串
     */
    public void broadcastDataToClients(String data) {
        if (connectedClients.isEmpty()) {
            if (listener != null) listener.onError("没有客户端连接，无法广播数据");
            return;
        }
        for (ConnectedThread clientThread : connectedClients.values()) {
            clientThread.write(data.getBytes());
        }
    }

    /**
     * 作为客户端，向服务端发送数据
     * @param data 要发送的字符串
     */
    public void sendDataToServer(String data) {
        if (clientConnectedThread != null) {
            clientConnectedThread.write(data.getBytes());
        } else {
            if (listener != null) listener.onError("未连接到服务端，无法发送数据");
        }
    }

    // ---------------------------------------------------------------------------------------------
    // 内部线程类，用于处理蓝牙连接和数据传输
    // ---------------------------------------------------------------------------------------------

    /**
     * 服务端监听连接的线程
     */
    private class AcceptThread extends Thread {
        private String SERVICE_NAME = "GameServer";

        public AcceptThread() {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                        if (listener != null) listener.onError("权限不足，无法创建服务端Socket");
                        return; // 提前返回
                    }
                } else {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                        if (listener != null) listener.onError("权限不足，无法创建服务端Socket");
                        return; // 提前返回
                    }
                }
                serverSocket = mAdapter.listenUsingRfcommWithServiceRecord(SERVICE_NAME, GAME_UUID);
                if (listener != null) listener.onServerStarted();
                Toast.makeText(mContext, "服务端已启动，等待连接...", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                if (listener != null) listener.onError("服务端Socket创建失败: " + e.getMessage());
                serverSocket = null;
            }
        }

        public void run() {
            BluetoothSocket socket;
            while (serverSocket != null) {
                try {
                    socket = serverSocket.accept(); // 阻塞，直到有客户端连接
                } catch (IOException e) {
                    if (listener != null) listener.onError("服务端accept失败: " + e.getMessage());
                    break;
                }

                if (socket != null) {
                    // 连接成功，为每个客户端创建一个独立的通信线程
                    manageConnectedSocket(socket, true); // true 表示是服务端接收的连接
                }
            }
            closeServerSocket(); // 循环结束时关闭服务端Socket
        }

        // 取消监听Socket
        public void cancel() {
            closeServerSocket();
        }

        private void closeServerSocket() {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                    serverSocket = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (listener != null) listener.onError("关闭服务端Socket失败: " + e.getMessage());
            }
        }
    }

    /**
     * 客户端连接服务端的线程
     */
    private class ConnectThread extends Thread {
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        if (listener != null) listener.onError("权限不足，无法创建客户端Socket");
                        return; // 提前返回
                    }
                }
                tmp = device.createRfcommSocketToServiceRecord(GAME_UUID);
            } catch (IOException e) {
                if (listener != null) listener.onError("客户端Socket创建失败: " + e.getMessage());
            }
            clientSocket = tmp; // 赋值给外部的clientSocket
        }

        public void run() {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "蓝牙权限不足", Toast.LENGTH_SHORT).show();
                return;
            }
            // 连接之前确保取消发现，因为它会降低连接速度
            if (mAdapter.isDiscovering()) {
                mAdapter.cancelDiscovery();
            }

            try {
                if (clientSocket == null) {
                    if (listener != null) listener.onError("客户端Socket未创建成功，无法连接");
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        if (listener != null) listener.onError("权限不足，无法连接设备");
                        return; // 提前返回
                    }
                }
                clientSocket.connect(); // 阻塞，直到连接成功或失败
                manageConnectedSocket(clientSocket, false); // false 表示是客户端发起的连接
            } catch (IOException connectException) {
                closeClientSocket();
                if (listener != null) listener.onError("连接失败: " + connectException.getMessage());
            }
        }

        public void cancel() {
            closeClientSocket();
        }

        private void closeClientSocket() {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                    clientSocket = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (listener != null) listener.onError("关闭客户端Socket失败: " + e.getMessage());
            }
        }
    }

    /**
     * 连接成功后的通信线程，用于读写数据
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final BluetoothDevice mmDevice;
        private final boolean isServerSide; // 标记此线程是服务端管理的客户端连接还是客户端连接服务端

        public ConnectedThread(BluetoothSocket socket, boolean isServer) {
            mmSocket = socket;
            mmDevice = socket.getRemoteDevice();
            isServerSide = isServer;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                if (listener != null) listener.onError("获取输入/输出流失败: " + e.getMessage());
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            if (listener != null) listener.onClientConnected(mmDevice, isServerSide);

            byte[] buffer = new byte[1024];
            int bytes;

            // 持续监听输入流
            while (true) {
                try {
                    bytes = mmInStream.read(buffer); // 阻塞，直到有数据
                    String data = new String(buffer, 0, bytes);
                    if (listener != null) listener.onDataReceived(mmDevice, data);
                } catch (IOException e) {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN)
                            != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(mContext, "蓝牙权限不足", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (listener != null) listener.onError("连接断开: " + mmDevice.getName() + " - " + e.getMessage());
                    // 连接断开后，从管理列表中移除此客户端
                    if (isServerSide) {
                        connectedClients.remove(mmDevice.getAddress());
                    } else {
                        // 如果是客户端模式，清空自身连接
                        clientConnectedThread = null;
                    }
                    if (listener != null) listener.onClientDisconnected(mmDevice);
                    break; // 退出循环
                }
            }
        }

        /**
         * 向输出流写入数据
         * @param bytes 要发送的字节
         */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
                mmOutStream.flush(); // 确保数据立即发送
            } catch (IOException e) {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext, "蓝牙权限不足", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (listener != null) listener.onError("发送数据到 " + mmDevice.getName() + " 失败: " + e.getMessage());
            }
        }

        /**
         * 关闭Socket和流
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                if (listener != null) listener.onError("关闭Socket失败: " + e.getMessage());
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // 辅助方法
    // ---------------------------------------------------------------------------------------------

    private void manageConnectedSocket(BluetoothSocket socket, boolean isServer) {
        ConnectedThread connectedThread = new ConnectedThread(socket, isServer);
        if (isServer) {
            // 如果是服务端，将此客户端线程加入管理列表
            connectedClients.put(socket.getRemoteDevice().getAddress(), connectedThread);
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this.mContext, "蓝牙权限不足", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(mContext, "客户端 " + socket.getRemoteDevice().getName() + " 已连接", Toast.LENGTH_SHORT).show();
        } else {
            // 如果是客户端，则设置其连接线程
            clientConnectedThread = connectedThread;
            Toast.makeText(mContext, "已连接到服务端 " + socket.getRemoteDevice().getName(), Toast.LENGTH_SHORT).show();
        }
        executorService.execute(connectedThread); // 在线程池中启动通信线程
    }


    public void setRequestEnableBtLauncher(ActivityResultLauncher<Intent> Launcher){
        this.requestEnableBtLauncher = Launcher;
    }

    // 新增方法：设置权限请求的 Launcher
    public void setRequestPermissionLauncher(ActivityResultLauncher<String[]> launcher) {
        this.requestPermissionLauncher = launcher;
    }

    // 需要在Activity或Fragment的onDestroy中调用此方法，以避免内存泄漏
    public void onDestroy() {
        if (acceptThread != null) {
            acceptThread.cancel();
        }
        if (clientConnectedThread != null) {
            clientConnectedThread.cancel();
        }
        for (ConnectedThread clientThread : connectedClients.values()) {
            clientThread.cancel();
        }
        connectedClients.clear();
        executorService.shutdownNow(); // 关闭线程池

        // 取消注册广播接收器
        try {
            if (mContext != null) { // 确保context不为null
                mContext.unregisterReceiver(discoveryReceiver);
            }
        } catch (IllegalArgumentException e) {
            // 如果接收器未注册，会抛出此异常，可以忽略
        }
    }

    public void cancelDiscovery() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this.mContext, "蓝牙权限不足", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mAdapter != null && mAdapter.isDiscovering()) {
            mAdapter.cancelDiscovery();
            Toast.makeText(mContext, "已取消蓝牙扫描", Toast.LENGTH_SHORT).show();
        }
        // 取消注册广播接收器，避免内存泄漏
        try {
            if (mContext != null) {
                mContext.unregisterReceiver(discoveryReceiver);
            }
        } catch (IllegalArgumentException e) {
            // 如果接收器未注册，会抛出此异常，可以忽略
        }
    }

}