package me.hupeng.app.wechat.UI.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import me.hupeng.app.wechat.R;
import me.hupeng.app.wechat.UI.LoginActivity;
import me.hupeng.app.wechat.chat.bean.User;
import me.hupeng.app.wechat.service.UserService;
import org.xutils.image.ImageOptions;
import org.xutils.x;

/**
 * Created by admin on 2017/8/7.
 */
public class UserFragment extends Fragment{
    private EditText etNickName;
    private TextView tvSex;
    private EditText etBirthday;
    private EditText etPhone;
    private EditText etEmail;
    private TextView tvUsername;
    private ImageView ivUserLogo;
    private Button btnLogout;
    private Button btnModify;

    /**
     * 是否可以修改个人信息
     * */
    private boolean canModify = false;

    private ImageOptions imageOptions = new ImageOptions.Builder()
            // 加载中或错误图片的ScaleType
            .setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
            // 默认自动适应大小
            // .setSize(...)
            .setIgnoreGif(false)
            // 如果使用本地文件url, 添加这个设置可以在本地文件更新后刷新立即生效.
            .setUseMemCache(false)
            .setImageScaleType(ImageView.ScaleType.CENTER_CROP).build();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user,container,false);

        //初始化控件
        etNickName = (EditText) view.findViewById(R.id.et_nick_name);
        tvSex = (TextView) view.findViewById(R.id.tv_sex);
        etBirthday = (EditText) view.findViewById(R.id.et_birthday);
        etPhone = (EditText) view.findViewById(R.id.et_phone);
        etEmail = (EditText) view.findViewById(R.id.et_email);
        tvUsername = (TextView) view.findViewById(R.id.tv_username);
        ivUserLogo = (ImageView) view.findViewById(R.id.iv_user_logo);
        btnLogout = (Button) view.findViewById(R.id.btn_logout);
        btnModify = (Button) view.findViewById(R.id.btn_modify);

        //初始化控件变量
        User user = UserService.getCurrentUser();
        etNickName.setText(user.getNickName());
        tvSex.setText(user.isSex()?"男":"女");
        etPhone.setText(user.getPhone());
        etEmail.setText(user.getEmail());
        tvUsername.setText(user.getUsername());

        x.image().bind(ivUserLogo,user.getUserPhoto(), imageOptions);


        //设置控件的点击监听
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLogout.setEnabled(false);
                btnLogout.setText("注销中，请稍后");
                logout();
            }
        });

        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canModify = !canModify;
                if (canModify){
                    etNickName.setEnabled(true);
                    etEmail.setEnabled(true);
                    etPhone.setEnabled(true);

                }else {
                    //
                    etNickName.setEnabled(false);
                    etPhone.setEnabled(false);
                    etEmail.setEnabled(false);

                    //save user information

                }
            }
        });

        return view;
    }

    /**
     * 执行注销操作
     * */
    private void logout(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("config", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("username");
        editor.remove("ak");
        editor.commit();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

}