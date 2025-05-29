package com.example.cdd.View;

import android.content.Context;
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

public class LoginFragment extends BaseFragment {

    private static final String ARG_USER_TYPE = "user_type";
    private static final String ARG_AUTO_LOGIN = "auto_login";

    private String mUserType;
    private boolean mAutoLogin;

    private EditText editTextUsername;
    private EditText editTextPassword;
    private ImageView togglePassword;
    private boolean isPasswordVisible = false;
    private ImageView btnBack;
    private Button btnLogin;
    private Button btnRegister;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String userType, boolean autoLogin) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_TYPE, userType);
        args.putBoolean(ARG_AUTO_LOGIN, autoLogin);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserType = getArguments().getString(ARG_USER_TYPE);
            mAutoLogin = getArguments().getBoolean(ARG_AUTO_LOGIN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    //@Override
    protected int layoutId() {
        return R.layout.fragment_login;
    }

    //@Override
    protected void initView(View view) {
        editTextUsername = view.findViewById(R.id.editTextUsername);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        togglePassword = view.findViewById(R.id.togglePassword);
        btnBack = view.findViewById(R.id.btn_back);
        btnLogin = view.findViewById(R.id.btn_login);
        btnRegister = view.findViewById(R.id.btn_register);

        // 切换密码可见性
        togglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                editTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_eye_open);
            } else {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_eye_closed);
            }
            editTextPassword.setSelection(editTextPassword.length());
        });

        // 返回按钮
        btnBack.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });

        // 登录按钮
        btnLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            Toast.makeText(getContext(), "登录点击：" + username, Toast.LENGTH_SHORT).show();
            // TODO: 后续添加验证逻辑
        });

        // 注册按钮
        btnRegister.setOnClickListener(v -> {
            Toast.makeText(getContext(), "点击注册按钮", Toast.LENGTH_SHORT).show();
            // TODO: 可跳转至注册页面
        });
    }

    //@Override
    protected void initData(Context context) {
        // 初始化数据，如果有自动登录信息可以在这里处理
    }
}
