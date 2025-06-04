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
import com.example.cdd.Controller.LoginController;
import com.example.cdd.R;

public class LoginFragment extends BaseFragment implements View.OnClickListener {

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
    private LoginController loginController;

    private FrameLayout fragmentContainer;

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
        if (getArguments() != null)
        {
            mUserType = getArguments().getString(ARG_USER_TYPE);
            mAutoLogin = getArguments().getBoolean(ARG_AUTO_LOGIN);
        }
        loginController = new LoginController(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initView(view); // 一定要调用！
        return view;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_login;
    }

    @Override
    protected void initView(View view) {
        editTextUsername = view.findViewById(R.id.editTextUsername);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        togglePassword = view.findViewById(R.id.togglePassword);
        btnBack = view.findViewById(R.id.btn_back);
        btnLogin = view.findViewById(R.id.btn_login);

        // 所有按钮统一设置点击监听器
        togglePassword.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    @Override
    protected void initData(Context context) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.togglePassword) {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_eye_open);
            } else {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_eye_closed);
            }
            editTextPassword.setSelection(editTextPassword.length()); // 保持光标在末尾
        }
        else if (id == R.id.btn_back) {
            if (fragmentContainer == null) {
                fragmentContainer = getActivity().findViewById(R.id.framelayout);
            }
            if (fragmentContainer != null ) {
                fragmentContainer.setClickable(false); // 禁用点击拦截
                fragmentContainer.setVisibility(View.INVISIBLE); // 隐藏容器（仍保留布局空间）
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        }
        else if (id == R.id.btn_login) {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            // 先检查用户是否存在
            boolean userExists = loginController.checkUserExists(username);

            if (userExists) {
                // 用户存在，执行登录
                boolean loginSuccess = loginController.login(username, password).isSuccess();
                if (loginSuccess) {
                    Toast.makeText(requireContext(), "登录成功", Toast.LENGTH_SHORT).show();
                    navigateToMainPage(); // 登录成功后跳转到主页面
                } else {
                    Toast.makeText(requireContext(), "登录失败，密码错误", Toast.LENGTH_SHORT).show();
                }
            } else {
                // 用户不存在，执行注册
                boolean registerSuccess = loginController.registerresult(username, password);
                if (registerSuccess) {
                    Toast.makeText(requireContext(), "注册成功", Toast.LENGTH_SHORT).show();
                    // 注册成功后自动登录
                    navigateToMainPage();
                } else {
                    Toast.makeText(requireContext(), "注册失败，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 导航到主页面
    private void navigateToMainPage() {
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
}