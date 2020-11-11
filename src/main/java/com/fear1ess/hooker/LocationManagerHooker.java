package com.fear1ess.hooker;

import android.location.LocationManager;

import com.fear1ess.manager.DeviceInfoManager;
import com.swift.sandhook.annotation.HookClass;
import com.swift.sandhook.annotation.HookMethod;
import com.swift.sandhook.annotation.HookMethodBackup;

import java.lang.reflect.Method;

@HookClass(LocationManager.class)
public class LocationManagerHooker {
    @HookMethodBackup("getLatitude")
    static Method getLatitude_Backup;

    @HookMethodBackup("getLongitude")
    static Method getLongtitude_Backup;

    @HookMethod("getLatitude")
    public static double getLatitude(LocationManager thiz){
        return DeviceInfoManager.getInstance().getLatitude();
    }

    @HookMethod("getLongitude")
    public static double getLongitude(LocationManager thiz){
        return DeviceInfoManager.getInstance().getLongitude();
    }

}
