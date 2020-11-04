package com.fear1ess.hooker;

import com.fear1ess.manager.DeviceInfoManager;
import com.swift.sandhook.SandHook;
import com.swift.sandhook.annotation.HookClass;
import com.swift.sandhook.annotation.HookMethod;
import com.swift.sandhook.annotation.HookMethodBackup;

import java.lang.reflect.Method;
import java.net.NetworkInterface;

@HookClass(NetworkInterface.class)
public class NetworkInterfaceHooker {
    @HookMethodBackup("getHardwareAddress")
    static Method getHardwareAddress_Backup;

    @HookMethod("getHardwareAddress")
    public static byte[] getHardwareAddress(NetworkInterface thiz) throws Throwable {
        String niName = thiz.getName();
        if(niName.equals("wlan0")||niName.equals("eth0")){
            return DeviceInfoManager.getInstance().getMacAddress();
        }
        return (byte[]) SandHook.callOriginByBackup(getHardwareAddress_Backup,thiz);
    }
}
