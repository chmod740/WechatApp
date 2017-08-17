package me.hupeng.app.wechat.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import me.hupeng.app.wechat.R;
import me.hupeng.app.wechat.chat.bean.RecentChat;
import me.hupeng.app.wechat.util.TimeService;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.sql.Timestamp;
import java.util.List;

/**
 *
 */
public class ChatListViewAdapter extends BaseAdapter {
    private static final String TAG = "ChatListViewAdapter";
    private List<RecentChat> contentList;
//    private List<RecentChat> backContentList;
    private LayoutInflater layoutInflater;
    private ChatListClickListener chatListClickListener;
    private Context context;

    public ChatListViewAdapter(Context context, List<RecentChat> contentList, ChatListClickListener chatListClickListener){
        this.context = context;
        this.contentList = contentList;
        this.layoutInflater = LayoutInflater.from(context);
        this.chatListClickListener = chatListClickListener;
//        this.backContentList = contentList;
    }

    public void setRecentChats(List<RecentChat>recentChats){
        this.contentList = recentChats;
    }

    public class ViewHolder {

        ImageView ivUserLogo;
        TextView tvUnreadMessageCount;
        ImageView ivNewMessage;

        TextView tvNickName;
        TextView tvChatTime;
        TextView tvChatText;

    }


    @Override
    public int getCount() {
        return this.contentList.size();
    }

    @Override
    public Object getItem(int position) {
        return contentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.chat_list_item, null);
            holder = new ViewHolder();
            holder.ivUserLogo = (ImageView) convertView.findViewById(R.id.iv_user_logo);
            holder.tvUnreadMessageCount = (TextView) convertView.findViewById(R.id.tv_unread_message_count);
            holder.ivNewMessage = (ImageView) convertView.findViewById(R.id.iv_new_message);
            holder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nick_name);
            holder.tvChatTime = (TextView) convertView.findViewById(R.id.tv_chat_time);
            holder.tvChatText = (TextView) convertView.findViewById(R.id.tv_chat_text);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageOptions imageOptions = new ImageOptions.Builder()
                // 加载中或错误图片的ScaleType
                .setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                // 默认自动适应大小
                // .setSize(...)
                .setIgnoreGif(false)
                // 如果使用本地文件url, 添加这个设置可以在本地文件更新后刷新立即生效.
                .setUseMemCache(false)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP).build();

        x.image().bind(holder.ivUserLogo,contentList.get(position).getUserPhoto(), imageOptions);

        if (contentList.get(position).getUnreadMessageCount() > 0){
            holder.tvUnreadMessageCount.setBackground(context.getResources().getDrawable(R.drawable.shape_red_point));
            if (contentList.get(position).getUnreadMessageCount() < 100){
                holder.tvUnreadMessageCount.setText(contentList.get(position).getUnreadMessageCount() + "");
            }else {
                holder.tvUnreadMessageCount.setText("99+");
            }
        }else {
            holder.tvUnreadMessageCount.setText("");
            holder.tvUnreadMessageCount.setBackground(null);
        }
        holder.tvNickName.setText(contentList.get(position).getUsername());
        holder.tvChatTime.setText(TimeService.getIntervalTime(new Timestamp(contentList.get(position).getUpdateTime().getTime())));
        String chatText = contentList.get(position).getContent();
        holder.tvChatText.setText(chatText.length() <= 20?chatText:chatText.substring(0,20) + "...");

        return convertView;
    }
}
