package com.example.cdd.Controller;
import com.example.cdd.View.LoginFragment;
import com.example.cdd.Model.AuthManager;
public class LoginController extends BaseController<LoginFragment,AuthManager > {
    public LoginController() {
        super();
        model =new AuthManager();
    }
    public void initialize() {
        view.onCreateView(null,null,null);
    }
    public void onDestroy() {
        
    }
    public boolean loginresult(String userid, String password) {
        this.model.login(userid,password);
        if(this.model.getLoginResult())
        {
            return true;
        }
        else
        {
            return false;
        }
       
    }
    public boolean registerresult(String userid, String password) {
        if(this.model.register(userid,password))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    // 负责登录/注册界面的逻辑
}

