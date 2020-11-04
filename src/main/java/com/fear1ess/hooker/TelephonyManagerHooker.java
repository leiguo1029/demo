package com.fear1ess.hooker;

import android.telephony.TelephonyManager;
import android.util.Log;

import com.fear1ess.manager.DeviceInfoManager;
import com.swift.sandhook.annotation.HookClass;
import com.swift.sandhook.annotation.HookMethod;
import com.swift.sandhook.annotation.HookMethodBackup;

import java.lang.reflect.Method;

@HookClass(TelephonyManager.class)
public class TelephonyManagerHooker {

    @HookMethodBackup("getDeviceId")
    static Method getDeviceId_Backup;

    @HookMethod("getDeviceId")
    public static String getDeviceId(TelephonyManager thiz){
        Log.d("fear1ess_hooker", "hook getDeviceId success");
        return DeviceInfoManager.getInstance().getImei();
    }

}
