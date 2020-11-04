package com.fear1ess.manager;

public class DeviceInfoManager {
    private static DeviceInfoManager mManager;

    //use for change deviceInfo to next item
    private static int position;

    public synchronized static DeviceInfoManager getInstance(){
        if(mManager == null){
            mManager = new DeviceInfoManager();
        }
        return mManager;
    }

    private DeviceInfoManager(){
        //read deviceInfos from file,database...
    }

    //imei
    public String getImei(){
        return "imei-001";
    }

    //android_id
    public String getAndroidId(){
        return "androidid-002";
    }

    //android序列号
    public String getSerialNumber(){
        return "serialnumber-003";
    }

    //Model
    public String getModel(){
        return "model-004";
    }

    public String getBrand(){
        return "brand-005";
    }

    public String getManufacturer(){
        return "manufacturer-006";
    }

    public String getCpuAbi(){
        return "cpuabi-007";
    }

    public String getOsVersion(){
        return "osversion-008";
    }

    public int getOsApi(){
        return 27;
    }

    public byte[] getMacAddress(){
        return new byte[]{127,127,127,127,127,127};
    }

    public int getWidthPixels(){
        return 1600;
    }

    public int getHeightPixels(){
        return 2500;
    }

}
