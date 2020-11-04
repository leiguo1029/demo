package com.fear1ess.hooker;

import android.content.ContentResolver;
import android.provider.Settings;

import com.fear1ess.manager.DeviceInfoManager;
import com.swift.sandhook.SandHook;
import com.swift.sandhook.annotation.HookClass;
import com.swift.sandhook.annotation.HookMethod;
import com.swift.sandhook.annotation.HookMethodBackup;
import com.swift.sandhook.annotation.MethodParams;

import java.lang.reflect.Method;

@HookClass(Settings.Secure.class)
public class SettingSecureHooker {
    @HookMethodBackup("getString")
    @MethodParams({ContentResolver.class,String.class})
    static Method getString_Backup;

    @HookMethod("getString")
    @MethodParams({ContentResolver.class,String.class})
    public static String getString(ContentResolver cr, String key) throws Throwable {
        if(key.equals(Settings.Secure.ANDROID_ID)){
            return DeviceInfoManager.getInstance().getAndroidId();
        }
        return (String) SandHook.callOriginByBackup(getString_Backup,cr,key);
    }
}
