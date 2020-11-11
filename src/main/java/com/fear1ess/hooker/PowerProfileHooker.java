package com.fear1ess.hooker;

import com.fear1ess.manager.DeviceInfoManager;
import com.mopub.common.util.DateAndTime;
import com.swift.sandhook.SandHook;
import com.swift.sandhook.annotation.HookMethod;
import com.swift.sandhook.annotation.HookMethodBackup;
import com.swift.sandhook.annotation.HookReflectClass;
import com.swift.sandhook.annotation.MethodParams;

import java.lang.reflect.Method;

@HookReflectClass("com.android.internal.os.PowerProfile")
public class PowerProfileHooker {
    @HookMethodBackup("getAveragePower")
    @MethodParams(String.class)
    static Method getAveragePower_Backup;

    @HookMethod("getAveragePower")
    @MethodParams(String.class)
    public static double getAveragePower(Object thiz,String str) throws Throwable {
        if(str.equals("battery.capacity")) return DeviceInfoManager.getInstance().getBatteryCapacity();
        return (double) SandHook.callOriginByBackup(getAveragePower_Backup,thiz,str);
    }
}
