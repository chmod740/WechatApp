package me.hupeng.app.wechat.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间服务
 * */
public class TimeService {

    /**
     * 得到间隔时间，例如20分钟前
     * */
    public static String getIntervalTime(Timestamp timestamp){
        long betweenTime = System.currentTimeMillis() - timestamp.getTime();
        //责任链

        //得到秒
        betweenTime = betweenTime / 1000;

        if (betweenTime < 60){
            return betweenTime + "秒前";
        }

        //得到分钟
        betweenTime = betweenTime / 60;

        if (betweenTime < 60){
            return betweenTime + "分钟前";
        }

        //得到小时
        betweenTime = betweenTime / 60;

        if (betweenTime < 24){
            return betweenTime + "小时前";
        }

        //得到天
        betweenTime = betweenTime / 24;

        if (betweenTime < 30){

            return betweenTime + "天前";
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date(timestamp.getTime()));
    }

}