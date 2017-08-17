package me.hupeng.app.wechat.UI;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import me.hupeng.app.wechat.R;
import me.hupeng.app.wechat.chat.bean.Contact;
import me.hupeng.app.wechat.service.UserService;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 同步联系人数据,请确认目前的登录状态
 */
@ContentView(R.layout.activity_sync_contact)
public class SyncContactActivity extends BaseActivity{


    @ViewInject(R.id.tv_show_msg)
    private TextView tvShowMsg;


//    private ProgressDialog prodialog;
    private DbManager db;
    private UserService userService;

    private void initial(){
        db = x.getDb(daoConfig);
        userService = new UserService();
        getUserRelationshipCount();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initial();
    }



    /**
     * 得到好友列表的数目
     * */
    private void getUserRelationshipCount(){
        userService.getUserRelationshipCount(getUserAccessKey(), new UserService.GetRelationshipCountListener() {
            @Override
            public void done(int count, Exception e) {
                if (e == null){
                    Log.i(TAG,"从服务器获取联系人数目成功,联系人数目：" + count);
                    try {
                        int nowCount = db.findAll(Contact.class).size();
                        if (nowCount != count){
                            Log.i(TAG,"本地联系人数目：" + nowCount + ",服务器联系人数目:"+ count +",服务器与本地不一致，需更新");
                            getUserRelationShip();
                        }else {
                            Log.i(TAG,"联系人数目与本地一致，无需更新");
                            tvShowMsg.setText("信息同步成功");
                            goHome();
                        }
                    } catch (DbException e1) {
                        e1.printStackTrace();
                    }
                }else {
                    tvShowMsg.setText("信息同步失败");
                    redirectCode = GO_LOGIN;
                    handler.sendEmptyMessageDelayed(0,500);
                }
            }
        });

    }

    /**
     * 更新好友列表的详细信息
     * */
    private void getUserRelationShip(){
        userService.getUserRelationship(getUserAccessKey(), new UserService.GetRelationshipListener() {
            @Override
            public void done(List<Contact> contacts, Exception e) {
                if (e == null){
                    //清除本地联系人数据
                    try {
                        List<Contact>localContacts = db.findAll(Contact.class);
                        for (Contact contact : localContacts){
                            db.delete(contact);
                        }
                    } catch (DbException e1) {
                        e1.printStackTrace();
                    }
                    Log.i(TAG,"清空本地联系人数据成功");

                    //添加新数据
                    if (contacts != null && contacts.size() > 0){
                        try {
                            for (Contact contact : contacts){
                                db.save(contact);
                            }
                            goHome();
                        } catch (DbException e1) {
                            e1.printStackTrace();
                        }
                    }
                    Log.i(TAG,"添加本地联系人数据成功");
                    goHome();
                }else {
                    tvShowMsg.setText("信息同步失败");
                    redirectCode = GO_LOGIN;
                    handler.sendEmptyMessageDelayed(0,500);
                }
            }
        });
    }






}
