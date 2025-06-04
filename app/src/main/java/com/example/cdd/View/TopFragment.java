package com.example.cdd.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.example.cdd.R;

public class TopFragment extends Fragment implements View.OnClickListener {
    private FrameLayout fragmentContainer;
    private ImageView btnBack;

    public TopFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top, container, false);
        initView(view);  // 一定要调用！
        return view;
    }
    protected int layoutId() {
        return R.layout.fragment_top;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initView(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
    }

    // 导航到主页面，请在此基础上进行修改
    private void BackToMain() {
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

    private void SwitchFragment(Fragment fragment) {
        if (fragmentContainer == null) {
            fragmentContainer = getActivity().findViewById(R.id.framelayout);
        }
        if (fragmentContainer != null && fragment != null) {
            fragmentContainer.setClickable(true); // 启用点击拦截
            fragmentContainer.setVisibility(View.VISIBLE); // 显示容器
            Fragment Fragment = new Fragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // 替换 FrameLayout 中的内容
            transaction.replace(R.id.framelayout, fragment);
            transaction.commit();
        }
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_back){
            BackToMain();
        }
    }
}