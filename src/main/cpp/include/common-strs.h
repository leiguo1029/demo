//
// Created by Fear1ess on 2020/10/30.
//

#ifndef DEMO_COMMON_STRS_H
#define DEMO_COMMON_STRS_H

const char* sysprop_keys[] = {
        "wifi.interface",
        "ro.build.version.sdk"
        "ro.product.brand",
        "ro.product.model",
        "ro.product.manufacturer",
        "ro.build.display.id",
        "ro.build.fingerprint",              //fp
        "ro.build.version.release",
        "ro.build.date.utc",
        "ro.product.board",
        "ro.product.device",
        "ro.product.name",
        "ro.board.platform",
        "gsm.sim.operator.numeric",
        "gsm.operator.numeric",
        nullptr
};

//检测andr
const char* vmCheck_sysprop_strs[] = {
        "init.svc.vbox86-setup",
        "init.svc.droid4x",
        "init.svc.qemud",
        "init.svc.su_kpbs_daemon",
        "init.svc.noxd",
        "init.svc.ttVM_x86-setup",
        "init.svc.xxkmsg",
        "init.svc.microvirtd",
        "ro.kernel.android.qemud",
        "androVM.vbox_dpi",
        "androVM.vbox_graph_mode",
        nullptr
};

//检测root/调试/usb连接状态
const char* phoneState_sysprop_strs[] = {
        "ro.secure",
        "ro.debuggable",
        "sys.usb.state",
        nullptr
};

//检测proxy代理
const char* proxy_sysprop_strs[] = {
        "http.proxyHost",
        "http.proxyPort",
        nullptr
};

const char* appinstalled_fstatat64_strs[] = {
        "/data/data/com.tencent.mm",
        "/data/data/com.tencent.mobileqq",
        "/data/data/com.sina.weibo",
        "/data/data/com.immomo.momo",
        "/data/data/cn.kuwo.player",
        "/data/data/com.tencent.qqmusic",
        "/data/data/com.netease.cloudmusic",
        "/data/data/com.sohu.inputmethod.sogou",
        "/data/data/com.baidu.input",
        "/data/data/com.iflytek.inputmethod",
        "/data/data/com.tencent.qqpinyin",
        "/data/data/com.eg.android.AlipayGphone",
        "/data/data/com.taobao.taobao",
        "/data/data/com.xunmeng.pinduoduo",
        "/data/data/com.jingdong.app.mall",
        "/data/data/com.baidu.searchbox",
        "/data/data/com.ss.android.article.news",
        "/data/data/com.tencent.news",
        nullptr
};

//自动化机刷工具检测
const char* checkauto_fstatat64_strs[] = {
        "/data/data/org.autojs.autojs",
        "/data/data/yk.juejin",
        "/data/data/com.cyjh.mobileanjian",
        "/data/data/com.cyjh.mobileanjianen",
        "/data/data/com.touchsprite.android",
        "/data/data/com.touchspriteent.android",
        "/data/data/net.aisence.Touchelper",
        "/data/data/com.touch.fairy",
        "/data/data/com.zdanjian.zdanjian",
        "/data/data/simplehat.clicker",
        nullptr
};

//root工具检测
const char* checksu_fstatat64_strs[] = {
        "/data/data/com.topjohnwu.magisk",
        nullptr
};

const char** fstatat64_check_strs_array[] = {
        checkauto_fstatat64_strs,
        checksu_fstatat64_strs,
        nullptr
};

const char* checksu_faccessat_strs[] = {
        "/system/xbin/bbksu",
        "/system/bin/shuamesu",
        "/system/bin/bdsu",
        "/system/xbin/bdsu",
        "/su/bin/su",
        "/system/bin/.su",
        "/system/xbin/ku.sud",
        "/system/xbin/bstk/su",
        "/system/bin/sudo",
        "/system/bin/sutemp",
        "/sbin/su",
        nullptr
};

const char* vpncheck_faccessat_strs[] = {
        "/sys/class/net/tun0",
        nullptr
};

const char* vmcheck_faccessat_strs[] = {
        "/dev/socket/qemud",
        "/dev/qemu_pipe",
        "/goldfish",
        "/system/lib/libc_malloc_debug_qemu.so",
        "/sys/qemu_trace",
        "/system/bin/microvirt-prop",
        "/system/lib/libdroid4x.so",
        "/system/bin/windroyed",
        "/system/bin/microvirtd",
        "/system/bin/nox-prop",
        "/system/bin/ttVM-prop",
        "/system/bin/droid4x-prop",
        "/data/.bluestacks.prop",
        "/data/app/com.bluestacks.appmart-1.apk",
        "/data/app/com.bluestacks.home-1.apk",
        "/data/app/com.bluestacks.searchapp-1.apk",
        "/data/data/com.bluestacks.setup",
        "/dev/vboxuser",
        "/fstab.vbox86",
        "/init.vbox86.rc",
        "/init.svc.vbox86-setup",
        "/sys/bus/pci/drivers/vboxguest/module",
        "/sys/bus/pci/drivers/vboxguest/new_id",
        "/sys/bus/pci/drivers/vboxguest/remove_id",
        "/sys/bus/pci/drivers/vboxguest/uevent",
        "/sys/bus/platform/drivers/qemu_pipe",
        "/sys/bus/platform/drivers/qemu_trace",
        "/init.svc.droid4x",
        "/init.svc.noxd",
        "/init.svc.microvirtd",
        "/ro.kernel.android.qemud",
        "/androVM.vbox_dpi",
        "/androVM.vbox_graph_mode",
        "/system/bin/qemu_props",
        "/system/bin/androVM-prop",
        "/system/lib/libaligl2.so",
        "/system/lib/libalitag.so",
        "/system/lib/libcloudletevent.so",
        "/system/lib/libcloudletos.so",
        "/system/lib/nemuguest.ko",
        "/system/lib/nemusf.ko",
        "/system/lib/nemuvideo.ko",
        "/system/lib/vpipe.ko",
        "/system/lib/vpipe_novt.ko",
        "/system/lib/vboxguest.ko",
        "/system/lib/vboxsf.ko",
        nullptr
};

const char** faccessat_check_strs_array[] = {
        checksu_faccessat_strs,
        vmcheck_faccessat_strs,
        vpncheck_faccessat_strs,
        nullptr
};


#endif //DEMO_COMMON_STRS_H
