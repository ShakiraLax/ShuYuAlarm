package com.sypm.shuyualarm;

import android.app.Application;

import com.sypm.shuyualarm.utils.Injection;
import com.sypm.shuyualarm.utils.ToastUtils;
import com.tumblr.remember.Remember;



/**
 * Created by Administrator on 2016/11/8.
 */

public class MyApplication extends Application {



    @Override
    public void onCreate() {
        super.onCreate();

        Injection.setApplicationContext(getApplicationContext());
        Remember.init(this, "com.sypm.shuyualarm");
        ToastUtils.init(this);
    }

    public void startAlarm() {

//        Log.i("闹钟服务", "startAlarm");
//        /**
//         首先获得系统服务
//         */
//        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//        /** 设置闹钟的意图，我这里是去调用一个服务，该服务功能就是获取位置并且上传*/
//        Intent intent = new Intent(this, LocationService.class);
//        PendingIntent pendSender = PendingIntent.getService(this, 0, intent, 0);
////        am.cancel(pendSender);
//
//        /**AlarmManager.RTC_WAKEUP 这个参数表示系统会唤醒进程；我设置的间隔时间是5分钟 */
//        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 5 * 60 * 1000, pendSender);
    }

    public void stopAlarm() {
//        Log.i("闹钟服务", "stopAlarm");
//        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, LocationService.class);
//        PendingIntent pendSender = PendingIntent.getService(this, 0, intent, 0);
//        am.cancel(pendSender);
//        Intent intent1 = new Intent(this, LocationService.class);
//        stopService(intent1);

    }

}
