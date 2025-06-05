package com.example.cdd.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.example.cdd.R;

import java.util.Objects;

public class DifficultyFragment extends Fragment implements View.OnClickListener {

    private Button button_easy;
    private Button button_middle;
    private Button button_difficult;
    private ImageView button_back;
    private String message;

    public DifficultyFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_difficulty, container, false);
        initView(view);  // 一定要调用！
        return view;
    }
    protected int layoutId() {
        return R.layout.fragment_difficulty;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initView(View view) {
        button_easy = view.findViewById(R.id.button);
        button_middle = view.findViewById(R.id.button4);
        button_difficult = view.findViewById(R.id.button1);
        button_back = view.findViewById(R.id.btn_back);
        button_easy.setOnClickListener(this);
        button_middle.setOnClickListener(this);
        button_difficult.setOnClickListener(this);
        button_back.setOnClickListener(this);
    }
    private FrameLayout fragmentContainer;
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
        if (view.getId() == R.id.button){
            //此处切换简单
            SingleplayerGameFragment fragment = new SingleplayerGameFragment();
            ((MainActivity) getActivity()).sendMessageToSingleplayerGameFragment(message, fragment);
            ((MainActivity) getActivity()).sendMessageToSingleplayerGameFragment("easy", fragment);
            SwitchFragment(fragment);
        }
        else if (view.getId() == R.id.button4){
            //此处切换中等
            SingleplayerGameFragment fragment = new SingleplayerGameFragment();
            ((MainActivity) getActivity()).sendMessageToSingleplayerGameFragment(message, fragment);
            ((MainActivity) getActivity()).sendMessageToSingleplayerGameFragment("medium", fragment);
            SwitchFragment(fragment);
        }
        else if (view.getId() == R.id.button1){
            //此处切换困难
            SingleplayerGameFragment fragment = new SingleplayerGameFragment();
            ((MainActivity) getActivity()).sendMessageToSingleplayerGameFragment(message, fragment);
            ((MainActivity) getActivity()).sendMessageToSingleplayerGameFragment("difficult", fragment);
            SwitchFragment(fragment);
        }
        else if (view.getId() == R.id.btn_back){
            //此处返回规则选择
            SwitchFragment(new RulesFragment());
        }
    }

    public void receiveMessage(String message) {
        this.message = message;
    }
}
