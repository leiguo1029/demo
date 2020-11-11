package com.fear1ess.hooker;

import android.graphics.Point;
import android.view.Display;

import com.fear1ess.manager.DeviceInfoManager;
import com.swift.sandhook.SandHook;
import com.swift.sandhook.annotation.HookClass;
import com.swift.sandhook.annotation.HookMethod;
import com.swift.sandhook.annotation.HookMethodBackup;
import com.swift.sandhook.annotation.MethodParams;

import java.lang.reflect.Method;

@HookClass(Display.class)
public class DisplayHooker {
    @HookMethodBackup("getRealSize")
    @MethodParams(Point.class)
    static Method getRealSize_Backup;

    @HookMethod("getRealSize")
    @MethodParams(Point.class)
    public static void getRealSize(Display thiz,Point point) throws Throwable {
        SandHook.callOriginByBackup(getRealSize_Backup,thiz,point);
        DeviceInfoManager dim = DeviceInfoManager.getInstance();
        point.x = dim.getWidthPixels();
        point.y = dim.getHeightPixels();
    }
}
