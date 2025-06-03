package com.example.cdd.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding; // **新增：导入 ViewBinding 基类**

// import butterknife.ButterKnife; // **删除：不再需要 ButterKnife**

// **修改：BaseFragment 变为泛型类，T 代表具体的 ViewBinding 类型**
public abstract class BaseFragment extends Fragment {

    protected final String TAG = this.getClass().getSimpleName();
    public Context context;

    private Toast toast;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    //由子类返回布局 ID
    protected abstract int layoutId();

    //由子类初始化视图控件
    protected abstract void initView(View view);

    // 原有方法：初始化数据
    protected abstract void initData(Context context);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(layoutId(), container, false);

        // 调用子类的方法初始化控件与数据
        initView(view);
        initData(context);

        return view;
    }
}
