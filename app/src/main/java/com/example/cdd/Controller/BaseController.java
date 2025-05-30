package com.example.cdd.Controller;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;



// BaseController 可以是一个抽象类，定义通用的生命周期方法和与 Model 的交互接口
public abstract class BaseController<V extends Fragment, M extends ViewModel> {
    protected V view;
    protected M model; // 可以是 ViewModel，也可以是直接的 Model 类

    public BaseController() {
    }

    public void attachView(V view) {
        this.view = view;
        onViewAttached();
    }

    public void detachView() {
        onViewDetached();
        this.view = null;
    }

    public void setModel(M model) {
        this.model = model;
    }

    // 当 View 被绑定时调用
    protected void onViewAttached() {}

    // 当 View 被解绑时调用
    protected void onViewDetached() {}

    // 抽象方法，强制子类实现各自的初始化逻辑
    public abstract void initialize();

    // 抽象方法，强制子类实现各自的清理逻辑
    public abstract void onDestroy();
}
