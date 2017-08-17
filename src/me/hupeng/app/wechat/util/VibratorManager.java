package me.hupeng.app.wechat.util;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by HUPENG on 2017/8/14.
 */
public class VibratorManager {
    public static void vibrate(Context context){
        Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        long [] pattern = {100,400,100,400};   // 停止 开启 停止 开启
        vibrator.vibrate(pattern,-1);
    }
}
