package me.hupeng.app.wechat.UI.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import me.hupeng.app.wechat.R;
import me.hupeng.app.wechat.UI.BaseFragment;
import me.hupeng.app.wechat.UI.ChatDetailActivity;
import me.hupeng.app.wechat.chat.ContactListViewAdapter;
import me.hupeng.app.wechat.chat.bean.Contact;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/8/11.
 */
public class ContactFragment extends BaseFragment {
    private ListView lvContact;
    private List<Contact>contacts;
    private ContactListViewAdapter contactListViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact,container,false);
        lvContact = (ListView)view.findViewById(R.id.lv_contact);
        return view;
    }



    private void initial(){
        DbManager db = x.getDb(daoConfig);
        try {
            contacts = db.findAll(Contact.class);
        } catch (DbException e) {
            contacts = new ArrayList<>();
            e.printStackTrace();
        }
        contactListViewAdapter = new ContactListViewAdapter(getActivity(),contacts);
        lvContact.setAdapter(contactListViewAdapter);
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
                intent.putExtra("id", contacts.get(i).getId());
                startActivity(intent);
            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initial();
    }
}
