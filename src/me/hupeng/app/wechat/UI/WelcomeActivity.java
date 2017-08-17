package me.hupeng.app.wechat.UI;

import android.os.Bundle;
import me.hupeng.app.wechat.R;
import me.hupeng.app.wechat.chat.bean.ChatDetail;
import me.hupeng.app.wechat.chat.bean.Contact;
import me.hupeng.app.wechat.chat.bean.RecentChat;
import me.hupeng.app.wechat.chat.bean.User;
import me.hupeng.app.wechat.service.UserService;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

/**
 * Created by admin on 2017/8/7.
 */
//@ContentView(R.layout.activity_welcome)
public class WelcomeActivity extends BaseActivity {


    private DbManager.DaoConfig daoConfig;
    private DbManager db ;
    private UserService userService = new UserService();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        File targetDir = new File(Environment.getExternalStorageDirectory().getPath() + "/");
        //隐藏标题栏以及状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        /**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效,需要去掉标题**/
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        handler.sendEmptyMessageDelayed(-1,2000);

        checkUserLoginStatus();
    }

    /**
     * 确定用户的登录状态
     * */
    private void checkUserLoginStatus(){
        userService.checkUserLoginStatus(getUserAccessKey(), new UserService.CheckLoginStatusListener() {
            @Override
            public void done(User user, Exception e) {
                if (e == null){
                    redirectCode = PAUSE_JUMP;
                    initialDB();
                    getUserRelationshipCount();
                }else {
                    redirectCode = GO_LOGIN;
//                    ToastManager.toast(WelcomeActivity.this,e.getMessage());
                }
            }
        });
    }

    /**
     * 初始化数据库
     * */
    private void initialDB(){
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
                db = x.getDb(daoConfig);
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
     * 得到好友列表的数目
     * */
    private void getUserRelationshipCount(){
        userService.getUserRelationshipCount(getUserAccessKey(), new UserService.GetRelationshipCountListener() {
            @Override
            public void done(int count, Exception e) {
                if (e == null){
                    try {
                        int nowCount = db.findAll(Contact.class).size();
                        if (nowCount != count){
                            goSyncContact();
//                            getUserRelationShip();
                        }else {

//                            redirectCode = GO_HOME;
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    try {
//                                        Thread.sleep(1000);
//                                    } catch (InterruptedException e1) {
//                                        e1.printStackTrace();
//                                    }
//                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            goHome();
//                                        }
//                                    });
//                                }
//                            }).start();

                            //GO_HOME
                            goHome();
                        }
                    } catch (DbException e1) {
                        e1.printStackTrace();
                    }
                }else {
                    goLogin();
                }
            }
        });

    }

//    /**
//     * 更新好友列表的详细信息
//     * */
//    private void getUserRelationShip(){
//        userService.getUserRelationship(getUserAccessKey(), new UserService.GetRelationshipListener() {
//            @Override
//            public void done(List<Contact> contacts, Exception e) {
//                if (contacts != null && contacts.size() > 0){
//                    try {
//                        for (Contact contact : contacts){
//                            db.save(contact);
//                        }
//                        goHome();
//                    } catch (DbException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//
//            }
//        });
//    }






}
