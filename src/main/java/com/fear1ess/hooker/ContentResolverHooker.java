package com.fear1ess.hooker;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;

import com.fear1ess.manager.DeviceInfoManager;
import com.swift.sandhook.SandHook;
import com.swift.sandhook.annotation.HookClass;
import com.swift.sandhook.annotation.HookMethod;
import com.swift.sandhook.annotation.HookMethodBackup;
import com.swift.sandhook.annotation.MethodParams;

import java.lang.reflect.Method;

@HookClass(ContentResolver.class)
public class ContentResolverHooker {
    @HookMethodBackup
    @MethodParams({Uri.class,String.class,String.class, Bundle.class})
    static Method call_Backup;

    @HookMethod
    public static Object call(Object thiz,Uri uri,String path,String value,Bundle bundle) throws Throwable {
        if(uri.getPath().equals("content://settings/system") &&
           path.equals("GET_secure") && value.equals("android_id")){
            return DeviceInfoManager.getInstance().getAndroidId();
        }
        return SandHook.callOriginByBackup(call_Backup,thiz,uri,path,value,bundle);
    }
}
