package me.hupeng.app.wechat.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import me.hupeng.app.wechat.R;
import me.hupeng.app.wechat.chat.bean.ChatDetail;
import me.hupeng.app.wechat.chat.bean.Contact;
import me.hupeng.app.wechat.chat.bean.User;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/8/11.
 */
public class ChatDetailListViewAdapter extends BaseAdapter {
    private Context context;
    private List<ChatDetail>contents;
    private ChatDetailListClickListener chatDetailListClickListener;
    private LayoutInflater inflater;

    private User me;
    private Contact other;

    private Map<Integer,ViewHolder>userToViewHolderMapping = new HashMap<>();

    public ChatDetailListViewAdapter(Context context, List<ChatDetail>list, User me, Contact other, ChatDetailListClickListener chatDetailListClickListener){
        this.context = context;
        this.contents = list;
        this.inflater = LayoutInflater.from(context);
        this.chatDetailListClickListener = chatDetailListClickListener;

        this.me = me;
        this.other = other;
    }

    public class ViewHolder {
        ImageView ivUserLogo;
        TextView tvNickName;
        TextView tvChatText;
    }

    @Override
    public int getCount() {
        return contents.size();
    }

    @Override
    public Object getItem(int i) {
        return contents.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
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

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;

        int type = getItemViewType(position);
        if (convertView == null) {
            holder = new ViewHolder();



            switch (type){
                case TYPE_SEND:
                    convertView = inflater.inflate(R.layout.chat_detail_list_item_right,null);
                    break;
                case TYPE_RECEIVE:
                    convertView = inflater.inflate(R.layout.chat_detail_list_item_left,null);
                    break;
            }


//            convertView = inflater.inflate(contents.get(position).isSend()?R.layout.chat_detail_list_item_right:R.layout.chat_detail_list_item_left, null);

            holder.ivUserLogo = (ImageView) convertView.findViewById(R.id.iv_user_logo);
            holder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nick_name);
            holder.tvChatText = (TextView) convertView.findViewById(R.id.tv_chat_text);

//            Picasso.with(context).load(contents.get(position).isSend()?me.getUserPhoto():other.getUserPhoto()).into(holder.ivUserLogo);
            x.image().bind(holder.ivUserLogo,contents.get(position).isSend()?me.getUserPhoto():other.getUserPhoto(), imageOptions);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.tvNickName.setText(contents.get(position).isSend()?me.getNickName():other.getNickName());
        switch (contents.get(position).getMsgType()){
            case 0x01:
                holder.tvChatText.setText(contents.get(position).getContent());
                break;
        }
        //设置点击事件的监听回调
        holder.ivUserLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatDetailListClickListener.onUserLogoShortClick(position,view);
            }
        });
        holder.ivUserLogo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                chatDetailListClickListener.onUserLogoLongClick(position,view);
                return false;
            }
        });
        holder.tvChatText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatDetailListClickListener.onChatTextShortClick(position,view);
            }
        });
        holder.tvChatText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                chatDetailListClickListener.onChatTextLongClick(position,view);
                return false;
            }
        });


//        convertView = inflater.inflate(contents.get(position).isSend()?R.layout.chat_detail_list_item_right:R.layout.chat_detail_list_item_left, null);
//
//        holder = new ViewHolder();
//        holder.ivUserLogo = (ImageView) convertView.findViewById(R.id.iv_user_logo);
//        holder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nick_name);
//        holder.tvChatText = (TextView) convertView.findViewById(R.id.tv_chat_text);
//        convertView.setTag(holder);

        //加载控件值




        return convertView;
    }



//    @Override
//    public View getView(int position, View convertView, ViewGroup viewGroup) {
//        ViewHolder holder = null;
//
//        //加载控件值
//        ImageOptions imageOptions = new ImageOptions.Builder()
//                // 加载中或错误图片的ScaleType
//                .setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
//                // 默认自动适应大小
//                // .setSize(...)
//                .setIgnoreGif(false)
//                // 如果使用本地文件url, 添加这个设置可以在本地文件更新后刷新立即生效.
//                .setUseMemCache(false)
//                .setImageScaleType(ImageView.ScaleType.CENTER_CROP).build();
//
//        if (convertView == null) {
//            convertView = inflater.inflate(contents.get(position).isSend()?R.layout.chat_detail_list_item_right:R.layout.chat_detail_list_item_left, null);
//            holder = new ViewHolder();
//            holder.ivUserLogo = (ImageView) convertView.findViewById(R.id.iv_user_logo);
//            holder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nick_name);
//            holder.tvChatText = (TextView) convertView.findViewById(R.id.tv_chat_text);
//            x.image().bind(holder.ivUserLogo,contents.get(position).isSend()?me.getUserPhoto():other.getUserPhoto(), imageOptions);
//            holder.tvNickName.setText(contents.get(position).isSend()?me.getNickName():other.getNickName());
//            switch (contents.get(position).getMsgType()){
//                case 0x01:
//                    holder.tvChatText.setText(contents.get(position).getContent());
//                    break;
//            }
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//
////        convertView = inflater.inflate(contents.get(position).isSend()?R.layout.chat_detail_list_item_right:R.layout.chat_detail_list_item_left, null);
////
////        holder = new ViewHolder();
////        holder.ivUserLogo = (ImageView) convertView.findViewById(R.id.iv_user_logo);
////        holder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nick_name);
////        holder.tvChatText = (TextView) convertView.findViewById(R.id.tv_chat_text);
////        convertView.setTag(holder);
//
//
//
//
//        //设置点击事件的监听回调
//
//        return convertView;
//    }

    private static final int TYPE_SEND = 0X00;
    private static final int TYPE_RECEIVE = 0X01;


    @Override
    public int getItemViewType(int position) {
        if (contents.get(position).isSend()){
            return TYPE_SEND;
        }else {
            return TYPE_RECEIVE;
        }
    }

    @Override
    public int getViewTypeCount() {
//        return super.getViewTypeCount();
        return 2;
    }
}
