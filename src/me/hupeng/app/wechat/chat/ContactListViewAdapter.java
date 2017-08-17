package me.hupeng.app.wechat.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import me.hupeng.app.wechat.R;
import me.hupeng.app.wechat.chat.bean.Contact;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * Created by admin on 2017/8/14.
 */
public class ContactListViewAdapter extends BaseAdapter {
    private Context context;
    private List<Contact>contacts;
    private LayoutInflater inflater;


    public ContactListViewAdapter(Context context,List<Contact>contacts){
        this.context = context;
        this.contacts = contacts;
        this.inflater = LayoutInflater.from(context);
    }

    class ViewHolder{
        ImageView ivUserLogo;
        TextView tvNickName;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int i) {
        return contacts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.contact_list_item, null);
            holder = new ViewHolder();
            holder.ivUserLogo = (ImageView) convertView.findViewById(R.id.iv_user_logo);
            holder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nick_name);
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

        x.image().bind(holder.ivUserLogo,contacts.get(position).getUserPhoto(), imageOptions);
        holder.tvNickName.setText(contacts.get(position).getNickName() == null?contacts.get(position).getUsername():contacts.get(position).getNickName());
        return convertView;
    }
}
