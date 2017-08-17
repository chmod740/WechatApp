package me.hupeng.app.wechat.UI;

import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.*;

import me.hupeng.app.wechat.R;
import me.hupeng.app.wechat.chat.ChatDetailListClickListener;
import me.hupeng.app.wechat.chat.ChatDetailListViewAdapter;
import me.hupeng.app.wechat.chat.MessageWebSocketClient;
import me.hupeng.app.wechat.chat.Msg;
import me.hupeng.app.wechat.chat.bean.ChatDetail;
import me.hupeng.app.wechat.chat.bean.Contact;
import me.hupeng.app.wechat.chat.bean.RecentChat;
import me.hupeng.app.wechat.chat.bean.User;
import me.hupeng.app.wechat.service.ChatCoreService;
import me.hupeng.app.wechat.service.UserService;
import me.hupeng.app.wechat.util.ToastManager;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2017/8/10.
 */
@ContentView(R.layout.activity_chat_detail)
public class ChatDetailActivity extends BaseActivity {
    @ViewInject(R.id.btn_back)
    private ImageButton btnBackToChat;
    @ViewInject(R.id.lv_chat_detail)
    private ListView lvChatDetail;
    @ViewInject(R.id.btn_send)
    private Button btnSend;
    @ViewInject(R.id.et_message)
    private EditText etMessage;
    @ViewInject(R.id.tv_chat_name)
    private TextView tvChatName;

    private DbManager db;

    private User me;
    private Contact other;

    private List<ChatDetail>chatDetails;
    private ChatCoreService.ChatCoreBinder chatCoreBinder;
    private ChatDetailListViewAdapter chatDetailListViewAdapter;


    private BroadcastReceiver msgBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG,"广播收到一个消息:");
            Msg msg = (Msg) intent.getSerializableExtra("msg");
            Log.i(TAG,"发送方：" + msg.getFrom());
            Log.i(TAG,"消息内容:" + msg.getMessage());
            Log.i(TAG,"消息发送时间:" + new SimpleDateFormat("HH:mm:ss").format(msg.getSendTime()));

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
//            try {
//                db.save(chatDetail);
//                Log.i(TAG,"聊天记录已经保存至本地");
//            } catch (DbException e) {
//                e.printStackTrace();
//                Log.i(TAG,"聊天记录已经保存至本地失败");
//            }
            chatDetails.add(chatDetail);
            chatDetailListViewAdapter.notifyDataSetChanged();
        }
    };


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG,"ChatDetailActivity与ChatCoreService绑定成功");
            chatCoreBinder = (ChatCoreService.ChatCoreBinder) iBinder;
//            chatCoreBinder.addChatCoreServiceListener(chatCoreServiceListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG,"ChatDetailActivity与ChatCoreService取消绑定");
        }
    };

    private ChatCoreService.ChatCoreServiceListener chatCoreServiceListener = new ChatCoreService.ChatCoreServiceListener() {
        @Override
        public void onConnect() {

        }

        @Override
        public void onDisconnect() {

        }

        @Override
        public void onMessage(Object object) {

        }
    };

    /**
     * 返回聊天列表页面
     * */
    @Event(type = View.OnClickListener.class, value = R.id.btn_back)
    private void goRecentChatView(View view){
        finish();
    }


    private void initial(){
        try {
            chatDetails = db.selector(ChatDetail.class).where("fromUserId","=",other.getUserId()).or("toUserId","=",other.getUserId()).orderBy("id").findAll();
        } catch (Exception e) {
            e.printStackTrace();
            chatDetails = new ArrayList<>();
        }

        chatDetailListViewAdapter = new ChatDetailListViewAdapter(this,chatDetails,me,other, new ChatDetailListClickListener() {
            @Override
            public void onUserLogoLongClick(int position, View v) {

            }

            @Override
            public void onUserLogoShortClick(int position, View v) {

            }

            @Override
            public void onChatTextLongClick(int position, View v) {

            }

            @Override
            public void onChatTextShortClick(int position, View v) {

            }
        });

        lvChatDetail.setAdapter(chatDetailListViewAdapter);
//        lvChatDetail.setSelection(lvChatDetail.getCount());
        //更新最近会话的数据库信息
        try {
            RecentChat recentChat = db.selector(RecentChat.class).where("userId","=",other.getUserId()).findFirst();
            recentChat.setUnreadMessageCount(0);
            db.update(recentChat);
        }catch (Exception e){

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化DB
        db = x.getDb(daoConfig);

        int id = getIntent().getIntExtra("id", 0);

        me = UserService.getCurrentUser();
        try {
            other = db.findById(Contact.class,id);
            tvChatName.setText(other.getNickName());
        } catch (Exception e) {
            e.printStackTrace();
            goHome();
        }
        MainActivity.CHAT_ID = other.getUserId();

        //启动服务相关操作
        Intent intent = new Intent(this,ChatCoreService.class);
        startService(intent);
        bindService(intent,serviceConnection,BIND_AUTO_CREATE);

        //注册广播
        IntentFilter intentFilter = new IntentFilter("me.hupeng.app.weather.msg_arrived");
        registerReceiver(msgBroadcastReceiver,intentFilter);

        //其他初始化操作
        initial();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //服务的销毁
//        chatCoreBinder.removeChatCoreServiceListener(chatCoreServiceListener);
        unbindService(serviceConnection);

        //广播反注册
        unregisterReceiver(msgBroadcastReceiver);

        //其他销毁操作
        MainActivity.CHAT_ID = 0;

    }

    @Event(value = R.id.btn_send, type = View.OnClickListener.class)
    private void onBtnSendClick(View view){
        if (etMessage.getText().toString().length() == 0){
            ToastManager.toast(this,"发送消息内容不能为空");
            return;
        }

        Msg msg = new Msg();
        msg.setMessage(etMessage.getText().toString());
        msg.setOperate(Msg.SEND_MESSAGE);
        msg.setSendTime(new Date(System.currentTimeMillis()));
        msg.setFrom(me.getId());
        msg.setTo(other.getUserId());

        ChatDetail chatDetail = new ChatDetail();
        chatDetail.setFromUserId(me.getId());
        chatDetail.setToUserId(other.getUserId());
        chatDetail.setCreateTime(new Date(System.currentTimeMillis()));
        chatDetail.setStatus(-1);
        chatDetail.setContent(msg.getMessage());
        chatDetail.setMsgType(0x01);
        chatDetail.setRead(true);
        chatDetail.setSend(true);

        long rTs = chatCoreBinder.sendMsg(msg, new MessageWebSocketClient.SendMessageResultListener() {
            @Override
            public void onSuccess(long ts) {

            }

            @Override
            public void onFail(long ts) {
            }
        });
        chatDetail.setTs(rTs);
        try {
            db.save(chatDetail);
            Log.i(TAG,"聊天记录已经保存至本地");
        } catch (DbException e) {
            e.printStackTrace();
            Log.i(TAG,"聊天记录已经保存至本地失败");
        }
        chatDetails.add(chatDetail);
        chatDetailListViewAdapter.notifyDataSetChanged();
        etMessage.setText("");
    }
}
