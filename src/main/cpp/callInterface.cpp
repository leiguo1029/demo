#include <jni.h>
#include <dlfcn.h>
#include <cstdlib>
#include <string>
#include <asm-arm-unistd.h>
#include <android/log.h>
#include <sys/system_properties.h>
#include <linux/fcntl.h>
#include <filesystem>
#include <fstream>
#include <unistd.h>
#include <fcntl.h>
#include "include/dobby.h"
#include "include/asm-arm-unistd.h"
#include "include/common-strs.h"
#include "include/common-data.h"
#include "include/callJavaMethod.h"

using namespace std;

#define TAG1 "adsdkhook"
#define TAG2 "nms_decStr"
#define TAG3 "nms_syscall"
#define TAG4 "nms_sysprop"
#define TAG5 "nms_enc"
#define LOGD(tag,fmt,args...) __android_log_print(ANDROID_LOG_DEBUG, (const char*)tag, (const char *) fmt, ##args)

extern "C" void *fake_dlopen(const char *libpath, int flags);
extern "C" void *fake_dlsym(void *handle, const char *name);



int fd_array[MAX_SIZE] = {0};
jobject g_DIM;
ctx* nms_handle = nullptr;

long (*ori_syscall)(long,...);
long __attribute((naked,pure)) fake_syscall(long number, ...){

}



void* (*ori_dlopen)(char*,int);
void* fake_dlopen_1(char* libPath,int mode){
    LOGD(TAG1,"loadlibrary by dlopen: %s",libPath);
    return ori_dlopen(libPath,mode);
}

int (*ori_decStrFunc)(int,int,char*,int,char* str);
int fake_decStrFunc(int a,int b,char* c,int d, char* str){
    int result = ori_decStrFunc(a,b,c,d,str);
    LOGD(TAG2,"decStr: %s",str);
    return result;
}

void copySyscallArgsToStack(va_list args,int* stack,int copySize){
    for(int i=0;i<copySize;++i){
        stack[i] = va_arg(args,int);
    }
}

int (*ori_nms_syscall)(int cmd,int,int,int,int,int,int,int);
int fake_nms_syscall(int cmd,...){

    va_list args_0;
    va_start(args_0,cmd);
    int args[7];
    copySyscallArgsToStack(args_0,args,7);
    va_end(args_0);

    int realCmd = cmd - 233;
    switch(realCmd){
        case __NR_read:{
            int fd = args[0];
            if(fd == fd_array[INDEX_PROCSELFSTATUS]){
                char* readBuf = (char*)args[1];
                int readSize = ori_nms_syscall(cmd,args[0],args[1],args[2],args[3],args[4],args[5],args[6]);
                if(readSize>=0 && strstr(readBuf,"TracerPid") != 0){
                    char tracerInfo[] = "TracerPid: 0";
                    memcpy(readBuf,tracerInfo,strlen(tracerInfo));
                    return strlen(tracerInfo);
                }
                return readSize;
            }
            break;
        }
        case __NR_open:{
            const char* path = (const char*)args[0];
            LOGD(TAG3,"open: %s",path);
            break;
        }
        case __NR_close:{
            int fd = args[0];
            if(fd == fd_array[INDEX_PROCSELFSTATUS]) fd_array[INDEX_PROCSELFSTATUS] = -1;
            break;
        }
        case __NR_openat:{
            int fd = args[0];
            const char* path = (const char*)args[1];
            LOGD(TAG3,"openat: %s",path);

            if(fd == AT_FDCWD && strcmp(path,"/proc/self/status") == 0){
                int res_fd = ori_nms_syscall(cmd,args[0],args[1],args[2],args[3],args[4],args[5],args[6]);
                if(res_fd >= 0) fd_array[0] = res_fd;
                return res_fd;
            }
            break;
        }
        case __NR_openat2:{
            int fd = args[0];
            const char* path = (const char*)args[1];
            LOGD(TAG3,"openat2: %s",path);
            break;
        }
        case __NR_faccessat:{
            int fd = args[0];
            const char* path = (const char*)args[1];
            LOGD(TAG3,"faccessat: %s",path);
            char*** ppChars = (char***)faccessat_check_strs_array;
            while(*ppChars != nullptr){
                char** pChars = *ppChars;
                while(*pChars != nullptr){
                    char* str = *pChars;
                    if(strcmp(path,str) == 0){
                        return -1;
                    }
                    ++pChars;
                }
                ++ppChars;
            }
            break;
        }
        case __NR_sysinfo:{
            struct sysinfo* si = (struct sysinfo*)args[0];
            int res = ori_nms_syscall(cmd,args[0],args[1],args[2],args[3],args[4],args[5],args[6]);
            if(res==0){
                unsigned long ram = si->totalram;
                unsigned long unitsize = si->mem_unit;

                LOGD(TAG3,"sysinfo   ram: %uld,mem_unit: %uld",ram,unitsize);

                si->totalram = callLongMethod(g_DIM,"getRAM","()J");
                si->mem_unit = callLongMethod(g_DIM,"getMemUnit","()J");
            }
            return res;
        }
        case __NR_statfs64:{
            const char* path = (const char*)args[0];
            struct statfs* sf = (struct statfs*)args[2];
            int res = ori_nms_syscall(cmd,args[0],args[1],args[2],args[3],args[4],args[5],args[6]);
            if(res != 0) return res;
            if(strcmp(path,"/data") == 0){
                sf->f_bsize = callLongMethod(g_DIM,"getDataDirSize","()J");
                sf->f_blocks = callLongMethod(g_DIM,"getDataDirBlocks","()J");
            }else if(strcmp(path,"/sdcard") == 0){
                sf->f_bsize = callLongMethod(g_DIM,"getSdcardSize","()J");
                sf->f_blocks = callLongMethod(g_DIM,"getSdcardBlocks","()J");
            }
            return res;
        }

        case __NR_uname:{
            utsname* un = (utsname*)args[0];
            int res = ori_nms_syscall(cmd,args[0],args[1],args[2],args[3],args[4],args[5],args[6]);
            if(res != 0) return res;
            getStringPropAndCopyToValue(un->sysname,"getUtsSysName");
            getStringPropAndCopyToValue(un->nodename,"getUtsNodeName");
            getStringPropAndCopyToValue(un->domainname,"getUtsDomainName");
            getStringPropAndCopyToValue(un->machine,"getUtsMachine");
            getStringPropAndCopyToValue(un->release,"getUtsRelease");
            getStringPropAndCopyToValue(un->version,"getUtsVersion");
            return res;
        }

        case __NR_socket:{
            int domain = args[0];
            int type = args[1];
            int protocol = args[2];
            if(domain == AF_NETLINK && type == SOCK_RAW && protocol == NETLINK_ROUTE){
            }
            break;
        }

        case __NR_bind:{
            break;
        }

        case __NR_sendmsg:{
            const msghdr* msg = (const msghdr*)args[1];
            LOGD("1","2");
            break;
        }

        case __NR_stat64:{
            const char* path = (const char*)args[0];
            LOGD(TAG3,"stat64: %s",path);
            break;
        }

        case __NR_fstatat64:{
            int fd = args[0];
            const char* path = (const char*)args[1];
            LOGD(TAG3,"fstatat64: %s",path);
            char*** ppChars = (char***)fstatat64_check_strs_array;
            while(*ppChars != nullptr){
                char** pChars = *ppChars;
                while(*pChars != nullptr){
                    char* str = *pChars;
                    if(strcmp(path,str) == 0){
                        return -1;
                    }
                    ++pChars;
                }
                ++ppChars;
            }
            break;
        }
        default:break;
    }
    return ori_nms_syscall(cmd,args[0],args[1],args[2],args[3],args[4],args[5],args[6]);
}

int (*ori_nms_sysprop_read) (char* __name, char* __value);

int fake_nms_sysprop_read(char* name, char* value){
    LOGD(TAG4,"read property from nms: %s",name);
    if(strcmp(name,"ro.build.version.sdk") == 0) return getIntPropAndCopyToValue(value,"getOsApi");
    if(strcmp(name,"ro.product.brand") == 0) return getStringPropAndCopyToValue(value,"getBrand");
    if(strcmp(name,"ro.product.model") == 0) return getStringPropAndCopyToValue(value,"getModel");
    if(strcmp(name,"ro.product.manufacturer") == 0) return getStringPropAndCopyToValue(value,"getManufacturer");
    if(strcmp(name,"ro.build.display.id") == 0) return getStringPropAndCopyToValue(value,"getDisplayId");
    if(strcmp(name,"ro.build.version.release") == 0) return getStringPropAndCopyToValue(value,"getOsRelease");
    if(strcmp(name,"ro.build.date.utc") == 0) return getStringPropAndCopyToValue(value,"getDataUtc");
    if(strcmp(name,"ro.build.fingerprint") == 0) return getStringPropAndCopyToValue(value,"getFingerprint");
    if(strcmp(name,"ro.product.board") == 0) return getStringPropAndCopyToValue(value,"getBoard");
    if(strcmp(name,"ro.product.device") == 0) return getStringPropAndCopyToValue(value,"getDevice");
    if(strcmp(name,"ro.product.name") == 0) return getStringPropAndCopyToValue(value,"getName");
    if(strcmp(name,"ro.board.platform") == 0) return getStringPropAndCopyToValue(value,"getPlatform");
    char** pStrs = (char**)phoneState_sysprop_strs;
    while(*pStrs != nullptr){
        char* str = *pStrs;
        if(strcmp(str,name) == 0){
            memcpy(value,"0",2);
            return 1;
        }
        ++pStrs;
    }
    return ori_nms_sysprop_read(name,value);
}

int (*ori_nms_readLine)(int fd,char* buf,int max_size);
int fake_nms_readLine(int fd,char* buf,int max_size){
    int result = ori_nms_readLine(fd,buf,max_size);
    if(fd == fd_array[INDEX_PROCSELFSTATUS]){
        if(result >= 0 && strstr(buf,"TracerPid")){
            LOGD(TAG1,"tracerBuf: %s",buf);
            char tracerInfo[] = "TracerPid: 0";
            memcpy(buf,tracerInfo,strlen(tracerInfo));
            return strlen(tracerInfo);
        }
    }
    return result;
}

int (*ori_nms_enc)(char* in,int inLen,char** pOut,int* pOutLen);
int fake_nms_enc(char* in,int inLen,char** pOut,int* pOutLen){
    LOGD(TAG5,"encData: %s",in);
    /*
    int fd = open("/sdcard/adsdk",O_RDWR|O_CREAT|O_APPEND,S_IRWXU);
    write(fd,in,strlen(in));
    write(fd,"\n\n",2);
    close(fd);*/
    return ori_nms_enc(in,inLen,pOut,pOutLen);
}

//hook android_dlopen_ext
void* (*ori_dlopen_ext)(char*,int,void*);
void* fake_dlopen_ext(char* libPath,int mode,void* extInfo){

    LOGD(TAG1,"loadlibrary by android_dlopen_ext: %s",libPath);
    void* handle =  ori_dlopen_ext(libPath,mode,extInfo);
    if(strstr(libPath,"libnms.so")){
        nms_handle = reinterpret_cast<ctx *>(fake_dlopen(libPath, RTLD_LAZY));
        void* decStrFunc = (char*)nms_handle->load_addr + 0x16c45;
        DobbyHook(decStrFunc,(void*)fake_decStrFunc,(void**)&ori_decStrFunc);
        void* nms_syscall = (char*)nms_handle->load_addr + 0x1f00;
        DobbyHook(nms_syscall,(void*)fake_nms_syscall,(void**)&ori_nms_syscall);
        void* nms_sysprop_read = (char*)nms_handle->load_addr + 0x21cf;
        DobbyHook(nms_sysprop_read,(void*)fake_nms_sysprop_read,(void**)&ori_nms_sysprop_read);

        //nms_read_line
        void* nms_readLine = (char*)nms_handle->load_addr + 0x4429;
        DobbyHook(nms_readLine,(void*)fake_nms_readLine,(void**)&ori_nms_readLine);

        //nms_enc
        void* nms_enc = (char*)nms_handle->load_addr + 0x5a69;
        DobbyHook(nms_enc,(void*)fake_nms_enc,(void**)&ori_nms_enc);
    }
    return handle;
}

// hook __system_property_get
int (*ori_system_property_get) (char* name,char* value);
int fake_system_property_get(char* name,char* value){
    LOGD(TAG4,"find property from libc: %s",name);
    char** pStrs = (char**)proxy_sysprop_strs;
    while(*pStrs != nullptr){
        char* str = *pStrs;
        if(strcmp(str,name) == 0){
            return 0;
        }
        ++pStrs;
    }
    return ori_system_property_get(name,value);
}

void doWork(){
    void* dl_handle = fake_dlopen("/system/lib/libdl.so",RTLD_LAZY);
    void* c_handle = fake_dlopen("/system/lib/libc.so",RTLD_LAZY);
    void* dlopen_ext = fake_dlsym(dl_handle,"android_dlopen_ext");
    void* system_property_get = fake_dlsym(c_handle,"__system_property_get");
    if(!dlopen_ext) {
        LOGD(TAG1,"dlopen_ext is null!!!");
        return;
    }
    DobbyHook((void*)dlopen_ext,(void*)fake_dlopen_ext,(void**)&ori_dlopen_ext);
  //  DobbyHook((void*)(dlopen), (void*)fake_dlopen_1, (void**)&ori_dlopen);
    DobbyHook((void*)(system_property_get), (void*)fake_system_property_get, (void**)&ori_system_property_get);
   // DobbyHook((void*)(syscall), (void*)(fake_syscall), (void**)&ori_syscall);
}

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm,void* reserved){
    JNIEnv* env = NULL;
    vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6);

    memset(fd_array,-1,sizeof(fd_array));

    jclass cls = env->FindClass("com/fear1ess/manager/DeviceInfoManager");
    jmethodID method = env->GetStaticMethodID(cls,"getInstance","()Lcom/fear1ess/manager/DeviceInfoManager;");
    jobject o = env->CallStaticObjectMethod(cls,method);
    g_DIM = env->NewGlobalRef(o);
    env->DeleteLocalRef(cls);
    env->DeleteLocalRef(o);
    saveJavaVM(vm);
    doWork();

    char** p  = environ;
    while(*p != nullptr){
        char* str = *p;
        LOGD(TAG1,"environ: %s",str);
        ++p;
    }

    return JNI_VERSION_1_6;
}