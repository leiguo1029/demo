package com.fear1ess.hooker;

import android.content.res.Resources;

import com.swift.sandhook.SandHook;
import com.swift.sandhook.wrapper.HookErrorException;

import java.net.NetworkInterface;

public class HookerManager {
    static Class<?>[] hookClasses = new Class<?>[]{
            TelephonyManagerHooker.class,
            SettingSecureHooker.class,
            NetworkInterfaceHooker.class,
            OsBuildHooker.class,
            ResourcesHooker.class,
            ContentResolverHooker.class
    };

    public static void init() {
        try {
            //patch fields
            FieldPatch.doPatch();
            //hook methods
            SandHook.addHookClass(hookClasses);
        } catch (HookErrorException e) {
            e.printStackTrace();
        }
    }
}
