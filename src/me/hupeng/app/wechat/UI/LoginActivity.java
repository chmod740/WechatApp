package me.hupeng.app.wechat.UI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import me.hupeng.app.wechat.R;
import me.hupeng.app.wechat.chat.bean.User;
import me.hupeng.app.wechat.service.UserService;
import me.hupeng.app.wechat.util.ToastManager;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by admin on 2017/8/12.
 */
@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity {
    @ViewInject(R.id.et_username)
    private EditText etUsername;
    @ViewInject(R.id.et_password)
    private EditText etPassword;
    @ViewInject(R.id.btn_login)
    private Button btnLogin;

    private UserService userService;

    @Event(value = R.id.btn_login, type = View.OnClickListener.class)
    private void onLoginButtonClick(View view){
        //dis able the button
        btnLogin.setEnabled(false);
        btnLogin.setBackground(getResources().getDrawable(R.drawable.shape_button_disable));

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        userService.login(username, password, new UserService.LoginListener() {
            @Override
            public void done(User user, Exception e) {
                btnLogin.setEnabled(true);
                btnLogin.setBackground(getResources().getDrawable(R.drawable.shape_button));
                if (e == null){
                    btnLogin.setText("登录成功");

                    saveUsernameAndAccessKey(user);

                    goSyncContact();
                }else {
                    ToastManager.toast(LoginActivity.this, e.getMessage());
                }
            }
        });

    }

    private void initial(){
        userService = new UserService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initial();
    }

    /**
     * 登录成功以后保存用户的登录信息至本地
     * */
    private void saveUsernameAndAccessKey(User user){
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("username", user.getUsername());
        editor.putString("ak",user.getAk());
        editor.commit();
    }
}
