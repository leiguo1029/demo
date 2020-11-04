package com.union_test.toutiao;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.fear1ess.hooker.HookerManager;
import com.locker.service.LockerService;
import com.locker.service.TraceService;
import com.locker.task.ExecuteTaskManager;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.xdandroid.hellodaemon.DaemonEnv;

/**
 * Create by hanweiwei on 11/07/2018
 */
@SuppressWarnings("unused")
public class DemoApplication extends MultiDexApplication {

    public static RefWatcher sRefWatcher = null;
    public static String PROCESS_NAME_XXXX = "process_name_xxxx";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (!LeakCanary.isInAnalyzerProcess(this)) {
            sRefWatcher = LeakCanary.install(this);
        }
        //锁屏场景demo相关配置
        LockerService.startService(this);
        ExecuteTaskManager.getInstance().init();
        DaemonEnv.initialize(this, TraceService.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        TraceService.sShouldStopService = false;
        DaemonEnv.startServiceMayBind(TraceService.class);

        //load native hook module
        System.loadLibrary("ry02");

        //hook java api
        HookerManager.init();

        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        Log.d("adsdkdemo_log", "imei: "+imei);

        String model = Build.MODEL;
        Log.d("adsdkdemo_log", "model: "+model);

        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("adsdkdemo_log", "android_id: "+android_id);

        //穿山甲SDK初始化
        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
        TTAdManagerHolder.init(this);
        //如果明确某个进程不会使用到广告SDK，可以只针对特定进程初始化广告SDK的content
        //if (PROCESS_NAME_XXXX.equals(processName)) {
        //   TTAdManagerHolder.init(this)
        //}
    }

}
