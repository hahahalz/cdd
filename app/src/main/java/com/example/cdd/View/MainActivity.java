package com.example.cdd.View;

import android.Manifest;
import android.app.Activity;
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
import android.widget.TextView;

import com.example.cdd.Controller.BluetoothController;
import com.example.cdd.Pojo.PlayerInformation;
import com.example.cdd.R;


public class  MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FrameLayout fragmentContainer;

    private BluetoothController mBluetoothController = new BluetoothController();   //蓝牙功能控制器

    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private static final int REQUEST_ADVERTISE = 100;        //用于权限检查
    private final String[] bluetoothPermissions = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        // 初始化 Launcher
        ActivityResultLauncher<Intent> Launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // 用户点击了"允许"，蓝牙已成功启用
                        mBluetoothController.turnOnBluetooth();
                        Toast.makeText(this, "蓝牙已启用", Toast.LENGTH_SHORT).show();
                        // 执行其他蓝牙相关操作（搜索设备，房间）
                        // startBluetoothDeviceDiscovery();
                        replaceFragement(new MultiplayerGameFragment());   // 跳转到多人游戏界面
                    } else {
                        // 用户拒绝了蓝牙启用请求
                        Toast.makeText(this, "蓝牙未启用，无法进入多人游戏", Toast.LENGTH_SHORT).show();
                    }
                });
        mBluetoothController.setRequestEnableBtLauncher(Launcher);


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

        button_multi.setOnClickListener(v -> showSimpleDialog());

        TextView loginStatus = findViewById(R.id.login_status);

// 从静态类 PlayerInformation 读取 userid 判断是否登录
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

    private void showSimpleDialog() {
        new AlertDialog.Builder(this)
                .setTitle("多人游戏")
                .setMessage("请选择您要进行的操作")
                .setPositiveButton("加入房间", (dialog, which) -> {
                    Toast.makeText(this, "请选择房间...", Toast.LENGTH_SHORT).show();
                })
                .setNeutralButton("使当前设备蓝牙可见", (dialog, which) -> {
                    Toast.makeText(this, "已开启!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("创建房间", (dialog, which) -> {
                    handleBluetoothAndStartMultiplayer();
                    Toast.makeText(this, "正在创建房间...", Toast.LENGTH_SHORT).show();
                })
                .setCancelable(false) // 禁止点击外部关闭
                .show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_login){
            replaceFragement(new LoginFragment());
        }
        else if (view.getId() == R.id.button_multi){
            handleBluetoothAndStartMultiplayer();
        }
        else if (view.getId() == R.id.button_single){
            replaceFragement(new RulesFragment());
        }
        else if(view.getId() == R.id.button_top){
            replaceFragement(new TopFragment());
        }
    }

    private void handleBluetoothAndStartMultiplayer() {
        if (!mBluetoothController.isSupportBluetooth()) {
            Toast.makeText(this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mBluetoothController.getBluetoothStatus()) {
            // 蓝牙已经开启，直接进入游戏
            replaceFragement(new MultiplayerGameFragment());
        } else {
            // 蓝牙未开启，检查并请求权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, bluetoothPermissions, REQUEST_BLUETOOTH_PERMISSIONS);
            } else {
                // 已有权限，请求开启蓝牙（但不立即跳转界面）
                mBluetoothController.turnOnBluetooth();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予权限，请求开启蓝牙
                mBluetoothController.turnOnBluetooth();
            } else {
                Toast.makeText(this, "需要蓝牙权限才能进行多人游戏", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showFragment(Fragment fragment) {
        fragmentContainer.setClickable(true); // 启用点击拦截
        fragmentContainer.setVisibility(View.VISIBLE); // 显示容器

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.framelayout, fragment)
                .commit();
    }

    // 隐藏 Fragment 时禁用拦截
    public void hideFragment() {
        fragmentContainer.setClickable(false); // 禁用点击拦截
        fragmentContainer.setVisibility(View.INVISIBLE); // 隐藏容器（仍保留布局空间）

        getSupportFragmentManager().beginTransaction()
                .remove(getSupportFragmentManager().findFragmentById(R.id.framelayout))
                .commit();
    }

    public void replaceFragement(Fragment fragment) {
        if (fragmentContainer == null) {
            fragmentContainer = findViewById(R.id.framelayout);
        }
        if (fragmentContainer != null && fragment != null) {
            fragmentContainer.setClickable(true); // 启用点击拦截
            fragmentContainer.setVisibility(View.VISIBLE); // 显示容器
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
}
