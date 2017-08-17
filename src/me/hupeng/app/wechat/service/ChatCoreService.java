package me.hupeng.app.wechat.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.hupeng.app.wechat.chat.MessageWebSocketClient;
import me.hupeng.app.wechat.chat.Msg;
import me.hupeng.app.wechat.chat.MyMessageWebSocketClient;
import me.hupeng.app.wechat.util.Toolkit;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2017/8/12.
 */
public class ChatCoreService extends Service implements MessageWebSocketClient.ConnectStatusListener,MessageWebSocketClient.MessageListener{
    private final String TAG = "###WEATHER_APP###";
    private List<ChatCoreServiceListener>chatCoreServiceListeners = new ArrayList<>();

    private ChatCoreBinder chatCoreBinder = new ChatCoreBinder();

    private MessageWebSocketClient messageWebSocketClient;

    private int userId;

    @Override
    public void onCreate() {
        userId = UserService.getCurrentUser().getId();
        messageWebSocketClient = MyMessageWebSocketClient.getInstance(UserService.getCurrentUser().getId());
        super.onCreate();
        messageWebSocketClient.setMessageDecoder(messageDecoder);
        messageWebSocketClient.setMessageEncoder(messageEncoder);
        messageWebSocketClient.addConnectStatusListener(this);
        messageWebSocketClient.addMessageListener(this);

        //发送心跳
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Msg msg = new Msg();
                        msg.setOperate(Msg.HEART_BEAT);
                        msg.setFrom(userId);
                        messageWebSocketClient.sendMessage(msg, new MessageWebSocketClient.SendMessageResultListener() {
                            @Override
                            public void onSuccess(long ts) {
                                Log.i(TAG,"心跳信息发送成功，时间：" + new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis())));
                            }

                            @Override
                            public void onFail(long ts) {
                                Log.i(TAG,"心跳信息发送失败，时间：" + new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis())));
                            }
                        });
                    }catch (Exception e){

                    }
                    try {
                        Thread.sleep(1l * 60 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    @Override
    public void onConnect() {
        //与服务器连接成功
        Log.i(TAG,"客户端成功连接服务器");
        Msg msg = new Msg();
        msg.setOperate(Msg.ON_LINE);
        msg.setFrom(userId);
        messageWebSocketClient.sendMessage(msg, new MessageWebSocketClient.SendMessageResultListener() {
            @Override
            public void onSuccess(long ts) {
//                Toast.makeText(MainActivity.this,"信息发送成功！",Toast.LENGTH_LONG).show();
                Log.i(TAG,"上线信息发送成功");
            }

            @Override
            public void onFail(long ts) {
//                Toast.makeText(MainActivity.this,"信息发送失败！",Toast.LENGTH_LONG).show();
                Log.i(TAG,"上线信息发送失败");
            }
        });

        for (ChatCoreServiceListener chatCoreServiceListener : chatCoreServiceListeners){
            chatCoreServiceListener.onConnect();
        }
    }

    @Override
    public void onDisconnect() {
        Log.i(TAG,"客户端与服务器连接断开");


        for (ChatCoreServiceListener chatCoreServiceListener : chatCoreServiceListeners){
            chatCoreServiceListener.onDisconnect();
        }
    }

    @Override
    public void onMessage(Object object) {
        Log.i(TAG,"客户端收到新消息");

        for (ChatCoreServiceListener chatCoreServiceListener : chatCoreServiceListeners){
            chatCoreServiceListener.onMessage(object);
        }
    }

    public interface ChatCoreServiceListener{
        public void onConnect();
        public void onDisconnect();
        public void onMessage(Object object);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return chatCoreBinder;
    }

    public class ChatCoreBinder extends Binder {
        public void addChatCoreServiceListener(ChatCoreServiceListener chatCoreServiceListener){
            if (chatCoreServiceListener != null){
                chatCoreServiceListeners.add(chatCoreServiceListener);
            }
        }

        public void removeChatCoreServiceListener(ChatCoreServiceListener chatCoreServiceListener){
            try{
                chatCoreServiceListeners.remove(chatCoreServiceListener);
            }catch (Exception e){

            }
        }

        //此处可能需要维护消息队列
        public long sendMsg(Msg msg , MessageWebSocketClient.SendMessageResultListener listener){
            long return_ts = messageWebSocketClient.sendMessage(msg, new MessageWebSocketClient.SendMessageResultListener() {
                @Override
                public void onSuccess(long ts) {
                    listener.onSuccess(ts);
                }

                @Override
                public void onFail(long ts) {
                    listener.onFail(ts);
                }
            });
            return return_ts;
        }
    }

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    MessageWebSocketClient.MessageDecoder messageDecoder = new MessageWebSocketClient.MessageDecoder() {
        @Override
        public Object decode(String s) {
            Msg msg =  gson.fromJson(s, Msg.class);

            //执行信息的解密操作
            String key = getUserAccessKey().substring(0,24);
            String decodeStr = Toolkit._3DES_decode(key.getBytes(),Toolkit.hexstr2bytearray(msg.getMessage()));
            msg.setMessage(decodeStr);
            return msg;
        }
    };

    MessageWebSocketClient.MessageEncoder messageEncoder = new MessageWebSocketClient.MessageEncoder() {
        @Override
        public String encode(Object s) {
            Msg msg = (Msg) s;
            if (msg.getOperate() == Msg.SEND_MESSAGE){
                String key = getUserAccessKey().substring(0,24);
                msg.setMessage(Toolkit._3DES_encode(key.getBytes(),msg.getMessage().getBytes()));
            }
            return gson.toJson(s);
        }
    };

    /**
     * 获取用户的Accesskey
     * */
    protected String getUserAccessKey(){
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        return sharedPreferences.getString("ak","");
    }
}
