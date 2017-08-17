package me.hupeng.app.wechat.UI.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import me.hupeng.app.wechat.R;
import me.hupeng.app.wechat.UI.BaseFragment;
import me.hupeng.app.wechat.UI.ChatDetailActivity;
import me.hupeng.app.wechat.chat.ChatListViewAdapter;
import me.hupeng.app.wechat.chat.Msg;
import me.hupeng.app.wechat.chat.bean.Contact;
import me.hupeng.app.wechat.chat.bean.RecentChat;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 模仿微信聊天列表界面
 * 策略：挂掉之前存储数据库
 * @author HUPENG
 */
public class ChatListFragment extends BaseFragment implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener {
    private ListView lvChatList;
    private ChatListViewAdapter chatListViewAdapter;
    private List<RecentChat>recentChats;
    private DbManager db;



    public void notifyMsgArrived(Msg msg, boolean read){
        Log.i(TAG,"聊天列表收到一个消息:");
        Log.i(TAG,"发送方：" + msg.getFrom());
        Log.i(TAG,"消息内容:" + msg.getMessage());
        Log.i(TAG,"消息发送时间:" + new SimpleDateFormat("HH:mm:ss").format(msg.getSendTime()));

        updateRecentChatList(msg,read);

        chatListViewAdapter.setRecentChats(recentChats);
        chatListViewAdapter.notifyDataSetChanged();
    }





    private void initial(){

        recentChats = new LinkedList<>();


        //设置ListView的适配器
        chatListViewAdapter = new ChatListViewAdapter(getActivity(),recentChats,null);
        lvChatList.setAdapter(chatListViewAdapter);

//        //注册消息广播
//        //随时接收新消息
//        IntentFilter intentFilter = new IntentFilter("me.hupeng.app.weather.msg_arrived");
//        getActivity().registerReceiver(msgBroadcastReceiver,intentFilter);


        //初始化DB
        db = x.getDb(daoConfig);

        //初始化最近聊天列表
        getRecentChatList();
        chatListViewAdapter.setRecentChats(recentChats);
        chatListViewAdapter.notifyDataSetChanged();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initial();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chat_list,container,false);

        lvChatList = (ListView) view.findViewById(R.id.lv_chat_list);

        lvChatList.setOnItemClickListener(this);
        lvChatList.setOnItemLongClickListener(this);

        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
        intent.putExtra("id", recentChats.get(i).getId());

        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        //反注册广播
//        getActivity().unregisterReceiver(msgBroadcastReceiver);
    }


    /**
     * 得到最新的聊天记录表
     * */
    private List<RecentChat> getRecentChatList(){
        try {
            recentChats = db.selector(RecentChat.class).orderBy("updateTime", true).findAll();

            for (RecentChat recentChat: recentChats){
                try {
                    Contact contact = db.selector(Contact.class).where("userId","=",recentChat.getUserId()).findFirst();
                    recentChat.setUserPhoto(contact.getUserPhoto());
                    recentChat.setUsername(contact.getUsername());
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
            Log.i(TAG,"从数据库初始化最近聊天列表失败");
            recentChats = new LinkedList<>();
        }
        return recentChats;
    }


    /**
     * 更新本地的列表
     * */
    public void updateRecentChatList(Msg msg, boolean read){
        try {
            RecentChat recentChat = db.selector(RecentChat.class).where("userId","=",msg.getFrom()).findFirst();
            if (recentChat == null){
                recentChat = new RecentChat();
                try {
                    Contact contact = db.selector(Contact.class).where("userId","=",msg.getFrom()).findFirst();
                    recentChat.setUserPhoto(contact.getUserPhoto());
                    recentChat.setUsername(contact.getUsername());
                    recentChat.setUserId(contact.getUserId());
                } catch (DbException e) {
                    e.printStackTrace();
                }
                recentChat.setContent(msg.getMessage());
                if (!read){
                    recentChat.setUnreadMessageCount(recentChat.getUnreadMessageCount()+1);
                }
                recentChat.setUpdateTime(new Date(System.currentTimeMillis()));
                db.save(recentChat);
            }else {
                try {
                    Contact contact = db.selector(Contact.class).where("userId","=",msg.getFrom()).findFirst();
                    recentChat.setUserPhoto(contact.getUserPhoto());
                    recentChat.setUsername(contact.getUsername());
                    recentChat.setUserId(contact.getUserId());
                } catch (DbException e) {
                    e.printStackTrace();
                }
                recentChat.setContent(msg.getMessage());
                if (!read){
                    recentChat.setUnreadMessageCount(recentChat.getUnreadMessageCount()+1);
                }
                recentChat.setUpdateTime(new Date(System.currentTimeMillis()));
                db.update(recentChat);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        getRecentChatList();
    }

    public void updateChatListView(){
        getRecentChatList();
        chatListViewAdapter.setRecentChats(recentChats);
        chatListViewAdapter.notifyDataSetChanged();
    }
}
