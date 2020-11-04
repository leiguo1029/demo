package com.fear1ess.hooker;

import com.fear1ess.manager.DeviceInfoManager;

import java.lang.reflect.Field;

public class FieldPatch {
    public static void setField(Object o,String fieldName,Object value){
        if(!(o instanceof Class)) o = o.getClass();
        Field f = null;
        try {
            f = ((Class<?>)o).getField(fieldName);
            f.setAccessible(true);
            f.set(o,value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
    public static void doPatch(){
        try {
            Class<?> c_build = Class.forName("android.os.Build");
            Class<?> c_build_ver = Class.forName("android.os.Build$VERSION");
            DeviceInfoManager dim = DeviceInfoManager.getInstance();

            //patch Build's fields
            setField(c_build,"MODEL",dim.getModel());
            setField(c_build,"BRAND",dim.getBrand());
            setField(c_build,"MANUFACTURER",dim.getManufacturer());
            setField(c_build,"CPU_ABI",dim.getCpuAbi());
            setField(c_build,"SERIAL",dim.getSerialNumber());

            //patch Build$VERSION's fields
            setField(c_build_ver,"RELEASE",dim.getOsVersion());
            setField(c_build_ver,"SDK_INT",dim.getOsApi());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
