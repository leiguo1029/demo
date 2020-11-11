package com.fear1ess.hooker;


import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.fear1ess.manager.DeviceInfoManager;
import com.swift.sandhook.SandHook;
import com.swift.sandhook.annotation.HookClass;
import com.swift.sandhook.annotation.HookMethod;
import com.swift.sandhook.annotation.HookMethodBackup;

import java.lang.reflect.Method;

@HookClass(Resources.class)
public class ResourcesHooker {
    @HookMethodBackup("getDisplayMetrics")
    static Method getDisplayMetrics_Backup;

    @HookMethod("getDisplayMetrics")
    public static DisplayMetrics getDisplayMetrics(Resources thiz) throws Throwable {
        DisplayMetrics dm = (DisplayMetrics) SandHook.callOriginByBackup(getDisplayMetrics_Backup,thiz);
        DeviceInfoManager dim = DeviceInfoManager.getInstance();
        dm.widthPixels = dim.getWidthPixels();
        dm.heightPixels = dim.getHeightPixels();
        dm.densityDpi = dim.getDensityDpi();
        return dm;
    }
}
