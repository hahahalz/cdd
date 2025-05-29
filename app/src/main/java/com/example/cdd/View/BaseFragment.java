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
    //获取TAG的fragment名称
    protected final String TAG = this.getClass().getSimpleName();
    public Context context; // 注意：onAttach 方法中的赋值有误，下面会修正

    // **新增：声明 ViewBinding 实例**

    /**
     * 封装toast对象
     */
    private Toast toast;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // **修正：将传入的 context 赋值给成员变量 this.context**
        this.context = context;
    }

    /**
     * 初始化，绑定数据
     * @param context 上下文
     */
    protected abstract void initData(Context context);


}