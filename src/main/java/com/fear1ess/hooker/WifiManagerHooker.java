package com.fear1ess.hooker;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.fear1ess.manager.DeviceInfoManager;
import com.swift.sandhook.annotation.HookClass;
import com.swift.sandhook.annotation.HookMethod;
import com.swift.sandhook.annotation.HookMethodBackup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@HookClass(WifiManager.class)
public class WifiManagerHooker {
    @HookMethodBackup("getScanResults")
    static Method getScanResults_Backup;

    @HookMethod("getScanResults")
    public static List getScanResults(WifiManager thiz) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String[] results =  DeviceInfoManager.getInstance().getScanResults();
        Class<?> cls = Class.forName("android.net.wifi.ScanResult");
        Constructor c = cls.getConstructor();
        c.setAccessible(true);
        List<ScanResult> li = new ArrayList<>();
        for(int i = 0;i < results.length;i += 2){
            ScanResult sr = (ScanResult) c.newInstance();
            sr.SSID = results[i];
            sr.BSSID = results[i+1];
            sr.level = 1;
            li.add(sr);
        }
        return li;
    }
}
