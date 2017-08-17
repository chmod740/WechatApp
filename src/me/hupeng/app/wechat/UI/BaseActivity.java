package me.hupeng.app.wechat.UI;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import me.hupeng.app.wechat.chat.bean.ChatDetail;
import me.hupeng.app.wechat.chat.bean.Contact;
import me.hupeng.app.wechat.chat.bean.RecentChat;
import org.xutils.DbManager;
import org.xutils.x;

/**
 * Created by admin on 2017/8/8.
 */
public class BaseActivity extends Activity {
    protected final int GO_HOME = 0x01;
    protected final int GO_LOGIN = 0x02;
    protected final int GO_SYNC_CONTACT = 0x03;
    protected final int PAUSE_JUMP = 0x04;

    protected int redirectCode = GO_LOGIN;

    protected final String TAG = "###WEATHER_APP###";

    protected DbManager.DaoConfig daoConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

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
        DbManager db = x.getDb(daoConfig);

        try {
            db.findAll(Contact.class).size();
        } catch (Exception e) {
            e.printStackTrace();
            try{
                db.save(new Contact());
                Contact contact = db.findFirst(Contact.class);
                db.delete(contact);
            }catch (Exception e1){
            }
        }

        try {
            db.findAll(RecentChat.class).size();
        } catch (Exception e) {
            e.printStackTrace();
            try{
                db.save(new RecentChat());
                RecentChat recentChat = db.findFirst(RecentChat.class);
                db.delete(recentChat);
            }catch (Exception e1){
            }
        }

        try {
            db.findAll(ChatDetail.class).size();
        } catch (Exception e) {
            e.printStackTrace();
            try{
                db.save(new ChatDetail());
                ChatDetail chatDetail = db.findFirst(ChatDetail.class);
                db.delete(chatDetail);
            }catch (Exception e1){
            }
        }
    }




    /**
     * 获取用户的Accesskey
     * */
    protected String getUserAccessKey(){
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        return sharedPreferences.getString("ak","");
    }

    /**
     * 获取用户的用户名
     * */
    protected String getUsername(){
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 跳到登录页面
     * */
    public void goLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 跳转到联系人同步页面
     * */
    public void goSyncContact(){
        Intent intent = new Intent(this, SyncContactActivity.class);
        startActivity(intent);
        finish();
    }
}
