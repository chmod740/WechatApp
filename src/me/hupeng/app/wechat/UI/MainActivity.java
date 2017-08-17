package me.hupeng.app.wechat.UI;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import me.hupeng.app.wechat.R;
import me.hupeng.app.wechat.UI.fragment.ChatListFragment;
import me.hupeng.app.wechat.UI.fragment.ContactFragment;
import me.hupeng.app.wechat.UI.fragment.ExploreFragment;
import me.hupeng.app.wechat.UI.fragment.UserFragment;
import me.hupeng.app.wechat.chat.Msg;
import me.hupeng.app.wechat.chat.bean.ChatDetail;
import me.hupeng.app.wechat.chat.bean.RecentChat;
import me.hupeng.app.wechat.service.ChatCoreService;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 主功能界面
 * */
@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {
    public   static int CHAT_ID = 0;

    //Fragments
//    private WeatherFragment weatherFragment;
    private ChatListFragment chatListFragment;
    private ContactFragment contactFragment;
    private ExploreFragment exploreFragment;
    private UserFragment userFragment;

//    //views
    @ViewInject(R.id.button1)
    private Button button1;
    @ViewInject(R.id.button2)
    private Button button2;
    @ViewInject(R.id.button3)
    private Button button3;
    @ViewInject(R.id.button4)
    private Button button4;

    @ViewInject(R.id.imageView1)
    private ImageView imageView1;
    @ViewInject(R.id.imageView2)
    private ImageView imageView2;
    @ViewInject(R.id.imageView3)
    private ImageView imageView3;
    @ViewInject(R.id.imageView4)
    private ImageView imageView4;

    @ViewInject(R.id.ll_chat)
    private LinearLayout llChat;
    @ViewInject(R.id.ll_contact)
    private LinearLayout llContact;
    @ViewInject(R.id.ll_explore)
    private LinearLayout llExplore;
    @ViewInject(R.id.ll_me)
    private LinearLayout llMe;
    @ViewInject(R.id.tv_unread_message_count)
    private TextView tvUnreadMessageCount;


    private DbManager db;


    private ChatCoreService.ChatCoreServiceListener chatCoreServiceListener = new ChatCoreService.ChatCoreServiceListener() {
        @Override
        public void onConnect() {
            Log.i(TAG,"客户端与服务器连接成功");
        }

        @Override
        public void onDisconnect() {
            Log.i(TAG,"客户端与服务器连接断开");
        }

        @Override
        public void onMessage(Object object) {
            Log.i(TAG,"客户端收到来自服务器端的消息：");
            Msg msg = (Msg)object;
            Log.i(TAG,"发送方：" + msg.getFrom());
            Log.i(TAG,"消息内容:" + msg.getMessage());
            Log.i(TAG,"消息发送时间:" + new SimpleDateFormat("HH:mm:ss").format(msg.getSendTime()));
            if (msg.getFrom() == CHAT_ID){
                sendBroadcast(msg,true);
                chatListFragment.notifyMsgArrived(msg,true);
            }else {
                chatListFragment.notifyMsgArrived(msg,false);
            }


            ChatDetail chatDetail = new ChatDetail();
            chatDetail.setFromUserId(msg.getFrom());
            chatDetail.setToUserId(msg.getTo());
            chatDetail.setCreateTime(new Date(System.currentTimeMillis()));
            chatDetail.setStatus(0);
            chatDetail.setContent(msg.getMessage());
            chatDetail.setMsgType(0x01);
            chatDetail.setRead(true);
            chatDetail.setSend(false);
            chatDetail.setTs(0);
            try {
                db.save(chatDetail);
                Log.i(TAG,"聊天记录已经保存至本地");
            } catch (DbException e) {
                e.printStackTrace();
                Log.i(TAG,"聊天记录已经保存至本地失败");
            }
            updateBottomMenuUnreadMessage(false);
        }
    };

    /**
     * 发送消息到达的广播
     * */
    private void sendBroadcast(Msg msg,boolean read){
        Intent intent = new Intent();
        intent.setAction("me.hupeng.app.weather.msg_arrived");
        Bundle bundle = new Bundle();
        bundle.putSerializable("msg", msg);
        bundle.putBoolean("read",read);
        intent.putExtras(bundle);
        intent.setPackage("me.hupeng.app.weather");
        MainActivity.this.sendBroadcast(intent);
    }


    private ChatCoreService.ChatCoreBinder chatCoreBinder = null;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG,"MainActivity与ChatCoreService取消绑定");

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG,"MainActivity与ChatCoreService绑定成功");
            chatCoreBinder = (ChatCoreService.ChatCoreBinder) service;
            chatCoreBinder.addChatCoreServiceListener(chatCoreServiceListener);
        }
    };

    //初始化 fragments
    private void initialFragments(){

        chatListFragment = new ChatListFragment();
        contactFragment = new ContactFragment();
        exploreFragment = new ExploreFragment();
        userFragment = new UserFragment();
    }

    private void oneOnClickListener(){
        imageView1.setBackground(getResources().getDrawable(R.drawable.icon_chat_green));
        imageView2.setBackground(getResources().getDrawable(R.drawable.icon_contact));
        imageView3.setBackground(getResources().getDrawable(R.drawable.icon_compass));
        imageView4.setBackground(getResources().getDrawable(R.drawable.icon_user));
        button1.setTextColor(Color.rgb(50,205,50));
        button2.setTextColor(Color.rgb(0, 0, 0));
        button3.setTextColor(Color.rgb(0, 0, 0));
        button4.setTextColor(Color.rgb(0, 0, 0));

        FragmentManager fm = getFragmentManager();
        // 开启Fragment事务
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.show(chatListFragment);
        transaction.hide(contactFragment);
        transaction.hide(exploreFragment);
        transaction.hide(userFragment);
        transaction.commit();
    }

    private void twoOnClickListener(){
        imageView1.setBackground(getResources().getDrawable(R.drawable.icon_chat));
        imageView2.setBackground(getResources().getDrawable(R.drawable.icon_contact_green));
        imageView3.setBackground(getResources().getDrawable(R.drawable.icon_compass));
        imageView4.setBackground(getResources().getDrawable(R.drawable.icon_user));
        button1.setTextColor(Color.rgb(0, 0, 0));
        button2.setTextColor(Color.rgb(50,205,50));
        button3.setTextColor(Color.rgb(0, 0, 0));
        button4.setTextColor(Color.rgb(0, 0, 0));

        FragmentManager fm = getFragmentManager();
        // 开启Fragment事务
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.hide(chatListFragment);
        transaction.show(contactFragment);
        transaction.hide(exploreFragment);
        transaction.hide(userFragment);
        transaction.commit();
    }

    private void threeOnClickListener(){
        imageView1.setBackground(getResources().getDrawable(R.drawable.icon_chat));
        imageView2.setBackground(getResources().getDrawable(R.drawable.icon_contact));
        imageView3.setBackground(getResources().getDrawable(R.drawable.icon_compass_green));
        imageView4.setBackground(getResources().getDrawable(R.drawable.icon_user));
        button1.setTextColor(Color.rgb(0, 0, 0));
        button2.setTextColor(Color.rgb(0, 0, 0));
        button3.setTextColor(Color.rgb(50,205,50));
        button4.setTextColor(Color.rgb(0, 0, 0));

        FragmentManager fm = getFragmentManager();
        // 开启Fragment事务
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.hide(chatListFragment);
        transaction.hide(contactFragment);
        transaction.show(exploreFragment);
        transaction.hide(userFragment);
        transaction.commit();
    }

    private void fourOnClickListener(){
        imageView1.setBackground(getResources().getDrawable(R.drawable.icon_chat));
        imageView2.setBackground(getResources().getDrawable(R.drawable.icon_contact));
        imageView3.setBackground(getResources().getDrawable(R.drawable.icon_compass));
        imageView4.setBackground(getResources().getDrawable(R.drawable.icon_user_green));
        button1.setTextColor(Color.rgb(0, 0, 0));
        button2.setTextColor(Color.rgb(0, 0, 0));
        button3.setTextColor(Color.rgb(0, 0, 0));
        button4.setTextColor(Color.rgb(50,205,50));

        FragmentManager fm = getFragmentManager();
        // 开启Fragment事务
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.hide(chatListFragment);
        transaction.hide(contactFragment);
        transaction.hide(exploreFragment);
        transaction.show(userFragment);
        transaction.commit();
    }

    private void addButtonsListener(){
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneOnClickListener();
            }
        });

        llChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneOnClickListener();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twoOnClickListener();
            }
        });

        llContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twoOnClickListener();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                threeOnClickListener();
            }
        });

        llExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                threeOnClickListener();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               fourOnClickListener();
            }
        });

        llMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fourOnClickListener();

            }
        });
    }


    /**
     * 设置默认Fragment
     */
    private void setDefaultFragment()
    {
        imageView1.setBackground(getResources().getDrawable(R.drawable.icon_chat_green));
        imageView2.setBackground(getResources().getDrawable(R.drawable.icon_contact));
        imageView3.setBackground(getResources().getDrawable(R.drawable.icon_compass));
        imageView4.setBackground(getResources().getDrawable(R.drawable.icon_user));
        button1.setTextColor(Color.rgb(50,205,50));
        button2.setTextColor(Color.rgb(0, 0, 0));
        button3.setTextColor(Color.rgb(0, 0, 0));
        button4.setTextColor(Color.rgb(0, 0, 0));

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();


        transaction.replace(R.id.fragment_main1, chatListFragment);
        transaction.replace(R.id.fragment_main2,contactFragment);
        transaction.replace(R.id.fragment_main3,exploreFragment);
        transaction.replace(R.id.fragment_main4, userFragment);

        transaction.hide(contactFragment);
        transaction.hide(exploreFragment);
        transaction.hide(userFragment);

        transaction.commit();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialFragments();
        addButtonsListener();
        setDefaultFragment();

        /**
         * 启动服务
         * */
        Intent intent = new Intent(MainActivity.this, ChatCoreService.class);
        startService(intent);
        bindService(intent,serviceConnection,BIND_AUTO_CREATE);

        /**
         * 初始化数据库
         * */
        db = x.getDb(daoConfig);
    }

    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        Intent intent = new Intent(MainActivity.this, ChatCoreService.class);
        stopService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateBottomMenuUnreadMessage(true);
    }

    private void updateBottomMenuUnreadMessage(boolean isChatListNeedToUpdate){
        //更新一下未读的消息的数量
        int unreadMessageCount = getUnreadMessageCount();
        if (unreadMessageCount == 0){
            tvUnreadMessageCount.setBackground(null);
            tvUnreadMessageCount.setText("");
        }else {
            tvUnreadMessageCount.setBackground(getResources().getDrawable(R.drawable.shape_red_point));
            tvUnreadMessageCount.setText(unreadMessageCount + "");
        }
        //通知ChatListFragment更新一下
        if (isChatListNeedToUpdate){
            chatListFragment.updateChatListView();
        }
    }

    /**
     * 获取到未读的消息的数目
     * */
    private int getUnreadMessageCount(){
        try {
            int sum = 0;
            List<RecentChat>recentChats = db.findAll(RecentChat.class);
            for (RecentChat recentChat: recentChats){
                sum += recentChat.getUnreadMessageCount();
            }
            return sum;
        } catch (DbException e) {
            e.printStackTrace();
            Log.i(TAG,"MainActivity获取未读消息数量失败");
            return 0;
        }
    }
}
