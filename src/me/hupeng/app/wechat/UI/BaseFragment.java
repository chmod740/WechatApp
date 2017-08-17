package me.hupeng.app.wechat.UI;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import org.xutils.DbManager;

/**
 * Created by admin on 2017/8/14.
 */
public class BaseFragment extends Fragment {
    protected final int GO_HOME = 0x01;
    protected final int GO_LOGIN = 0x02;
    protected final int GO_SYNC_CONTACT = 0x03;
    protected final int PAUSE_JUMP = 0x04;

    protected int redirectCode = GO_LOGIN;

    protected final String TAG = "###WEATHER_APP###";

    protected DbManager.DaoConfig daoConfig;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String dbName = "weather_app_" + getUsername() + ".db";
        daoConfig = new DbManager.DaoConfig()
                .setDbName(dbName)
                // 不设置dbDir时, 默认存储在app的私有目录.
//                .setDbDir(new File("/sdcard")) // "sdcard"的写法并非最佳实践, 这里为了简单, 先这样写了.
                .setDbVersion(2)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        // 开启WAL, 对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        // TODO: ...
                        // db.addColumn(...);
                        // db.dropTable(...);
                        // ...
                        // or
                        // db.dropDb();
                    }
                });
    }

    /**
     * 获取用户的Accesskey
     * */
    protected String getUserAccessKey(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("config", getActivity().MODE_PRIVATE);
        return sharedPreferences.getString("ak","");
    }

    /**
     * 获取用户的用户名
     * */
    protected String getUsername(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("config", getActivity().MODE_PRIVATE);
        return sharedPreferences.getString("username", "");
    }

    /**
     * 处理跳转相关的内容
     * */
    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (redirectCode){
                case GO_HOME:
                    goHome();
                    break;
                case GO_LOGIN:
                    goLogin();
                    break;
                case GO_SYNC_CONTACT:
                    goSyncContact();
                    break;
            }

            super.handleMessage(msg);
        }
    };


    /**
     * 跳转到主界面
     * */
    public void goHome(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
//        finish();
    }

    /**
     * 跳到登录页面
     * */
    public void goLogin(){
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
//        finish();
    }

    /**
     * 跳转到联系人同步页面
     * */
    public void goSyncContact(){
        Intent intent = new Intent(getActivity(), SyncContactActivity.class);
        startActivity(intent);
//        finish();
    }
}
