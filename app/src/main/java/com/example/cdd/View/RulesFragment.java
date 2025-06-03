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

import com.example.cdd.R;

public class RulesFragment extends Fragment implements View.OnClickListener {

    private Button button_north;
    private Button button_south;
    private ImageView button_back;

    public RulesFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rules, container, false);
        initView(view);  // 一定要调用！
        return view;
    }
    protected int layoutId() {
        return R.layout.fragment_rules;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initView(View view) {
        button_north = view.findViewById(R.id.button4);
        button_south = view.findViewById(R.id.button);
        button_back = view.findViewById(R.id.btn_back);
        button_north.setOnClickListener(this);
        button_south.setOnClickListener(this);
        button_back.setOnClickListener(this);
    }

    private void SwitchFragment(Fragment fragment) {
        Fragment Fragment = new Fragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 替换 FrameLayout 中的内容
        transaction.replace(R.id.framelayout,fragment);
        transaction.commit();
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button){
            //此处切换南方规则
            SwitchFragment(new DifficultyFragment());
        }
        else if (view.getId() == R.id.button4){
            //此处切换北方规则
            SwitchFragment(new DifficultyFragment());
        }
        else if (view.getId() == R.id.btn_back){
            Intent intent = new Intent(getActivity(), MainActivity.class);//返回主界面
            startActivity(intent);

            if (getActivity() != null) {
                getActivity().finish();
            }
        }

    }

}