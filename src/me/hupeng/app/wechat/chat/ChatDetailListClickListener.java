package me.hupeng.app.wechat.chat;

import android.view.View;

/**
 * Created by admin on 2017/8/11.
 */
public interface ChatDetailListClickListener {
    public void onUserLogoLongClick(int position, View v);
    public void onUserLogoShortClick(int position, View v);
    public void onChatTextLongClick(int position, View v);
    public void onChatTextShortClick(int position, View v);
}
