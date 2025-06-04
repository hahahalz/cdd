package com.example.cdd.View;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

import com.example.cdd.R;

public class  MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
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
    }

    private FrameLayout fragmentContainer;
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_login){
            replaceFragement(new LoginFragment());
        }
        else if (view.getId() == R.id.button_multi){
            replaceFragement(new MultiplayerGameFragment());
        }
        else if (view.getId() == R.id.button_single){
            replaceFragement(new RulesFragment());
        }
        else if(view.getId() == R.id.button_top){
            replaceFragement(new TopFragment());
        }
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
}