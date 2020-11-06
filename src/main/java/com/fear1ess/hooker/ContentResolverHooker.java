package com.fear1ess.hooker;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.fear1ess.manager.DeviceInfoManager;
import com.swift.sandhook.SandHook;
import com.swift.sandhook.annotation.HookClass;
import com.swift.sandhook.annotation.HookMethod;
import com.swift.sandhook.annotation.HookMethodBackup;
import com.swift.sandhook.annotation.MethodParams;


import java.lang.reflect.Method;

@HookClass(ContentResolver.class)
public class ContentResolverHooker {
    @HookMethodBackup("call")
    @MethodParams({Uri.class,String.class,String.class, Bundle.class})
    static Method call_Backup;

    @HookMethod("call")
    @MethodParams({Uri.class,String.class,String.class, Bundle.class})
    public static Bundle call(ContentResolver thiz,Uri uri,String method,String arg,Bundle bundle) throws Throwable {
        Log.d("fear1ess_hooker", "call ContentResolver.call... "+uri.getPath());
        uri.getAuthority();
        if(uri.getAuthority().equals("settings") && uri.getPath().equals("/system") &&
                method.equals("GET_secure") && arg.equals("android_id")) {
            Bundle bd = new Bundle();
            bd.putString("value",DeviceInfoManager.getInstance().getAndroidId());
            return bd;

        }
        return (Bundle) SandHook.callOriginByBackup(call_Backup,thiz,uri,method,arg,bundle);
    }
}
