package com.fear1ess.manager;

import android.net.wifi.ScanResult;

import java.lang.reflect.Constructor;
import java.util.List;

public abstract class DeviceInfoManager {
    private static DeviceInfoManager mManager = new DeviceInfoFromDbManager();

    //use for change deviceInfo to next item
    private static int position;

    public static DeviceInfoManager getInstance(){
        return mManager;
    }

    protected DeviceInfoManager(){
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

    public long getRAM() { return 666666666L; }

    public long getMemUnit() { return 4; }

    public long getDataDirSize() { return 777777777; }

    public long getDataDirBlocks() { return 4; }

    public long getSdcardSize() { return 888888888; }

    public long getSdcardBlocks() { return 4; }

    public String getDisplayId() { return "displayid-009"; }
    public String getOsRelease() { return "osrelease-010"; }
    public String getDataUtc() { return "datautc-011"; }
    public String getFingerprint() { return "fingerprint-012"; }
    public String getBoard() { return "board-013"; }
    public String getDevice() { return "device-014"; }
    public String getName() { return "name-015"; }
    public String getPlatform() { return "platform-016"; }

    public int getDensityDpi() {
        return 666;
    }
    public int getDensity() { return 640; }

    public double getBatteryCapacity() { return 3880.0; }

    //use for uname syscall
    public String getUtsSysName() { return "uts-sysname"; }
    public String getUtsNodeName() { return "uts-nodename"; }
    public String getUtsDomainName() { return "uts-domainname"; }
    public String getUtsMachine() { return "uts-machine"; }
    public String getUtsRelease() { return "uts-release"; }
    public String getUtsVersion() { return "uts-version"; }

    //aplist
    public String[] getScanResults() {
        return new String[]{"fear1ess","12:34:56:78:90:AB"};
    }

    public double getLatitude() { return 90.0; }
    public double getLongitude() { return 120.0; }
}
