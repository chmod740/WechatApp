package me.hupeng.app.wechat.chat;

import android.view.View;

/**
 * Created by HUPENG on 2017/8/10.
 */
public interface ChatListClickListener {
    public void onLongClick(int position, View v) ;
    public void onShortClick(int position, View v) ;
}
