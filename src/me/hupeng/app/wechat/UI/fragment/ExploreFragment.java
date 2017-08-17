package me.hupeng.app.wechat.UI.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import me.hupeng.app.wechat.R;

/**
 * Created by admin on 2017/8/11.
 */
public class ExploreFragment extends Fragment {
    private LinearLayout llWeather;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore,container,false);
        llWeather = (LinearLayout) view.findViewById(R.id.ll_weather);

        llWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goWeather();
            }
        });

        return view;
    }

    private void goWeather(){
//        Intent intent = new Intent(getActivity(), WeatherActivity.class);
//        startActivity(intent);
    }
}
