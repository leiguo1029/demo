package com.fear1ess.hooker;

import android.os.Build;

import com.fear1ess.manager.DeviceInfoManager;
import com.swift.sandhook.annotation.HookClass;
import com.swift.sandhook.annotation.HookMethod;
import com.swift.sandhook.annotation.HookMethodBackup;

import java.lang.reflect.Method;

@HookClass(Build.class)
public class OsBuildHooker {
    @HookMethodBackup("getSerial")
    static Method getSerial_Backup;

    @HookMethod("getSerial")
    public static String getSerial(){
        return DeviceInfoManager.getInstance().getSerialNumber();
    }
}
