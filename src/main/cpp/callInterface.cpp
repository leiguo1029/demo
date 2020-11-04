#include <jni.h>
#include <dlfcn.h>
#include <cstdlib>
#include <string>
#include <asm-arm-unistd.h>
#include <android/log.h>
#include <sys/system_properties.h>
#include "include/dobby.h"
#include "include/asm-arm-unistd.h"
#include "include/common-strs.h"

#define TAG1 "adsdkhook"
#define TAG2 "nms_decStr"
#define TAG3 "nms_syscall"
#define TAG4 "nms_sysprop"
#define LOGD(tag,fmt,args...) __android_log_print(ANDROID_LOG_DEBUG, (const char*)tag, (const char *) fmt, ##args)

extern "C" void *fake_dlopen(const char *libpath, int flags);
extern "C" void *fake_dlsym(void *handle, const char *name);

struct ctx {
    void *load_addr;
    void *dynstr;
    void *dynsym;
    void *symtab;
    void* strtab;
    int nsymtabs;
    int nsyms;
    off_t bias;
};

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
    va_list args_0,args;
    va_start(args_0,cmd);
    int stack[7];
    copySyscallArgsToStack(args_0,stack,7);
    va_end(args_0);

    va_start(args,cmd);

    int realCmd = cmd - 233;
    switch(realCmd){
        case __NR_open:{
            const char* path = va_arg(args,const char*);
            LOGD(TAG3,"open: %s",path);
            break;
        }
        case __NR_openat:{
            int fd = va_arg(args,int);
            const char* path = va_arg(args,const char*);
            LOGD(TAG3,"openat: %s",path);
            break;
        }
        case __NR_openat2:{
            int fd = va_arg(args,int);
            const char* path = va_arg(args,const char*);
            LOGD(TAG3,"openat2: %s",path);
            break;
        }
        case __NR_faccessat:{
            int fd = va_arg(args,int);
            const char* path = va_arg(args,const char*);
            LOGD(TAG3,"faccessat: %s",path);
            char*** ppChars = (char***)faccessat_check_strs_array;
            while(*ppChars != nullptr){
                char** pChars = *ppChars;
                while(*pChars != nullptr){
                    char* str = *pChars;
                    if(strcmp(path,str) == 0) return -1;
                    ++pChars;
                }
                ++ppChars;
            }
            break;
        }

        case __NR_stat64:{
            const char* path = va_arg(args,const char*);
            LOGD(TAG3,"stat64: %s",path);
            break;
        }
        case __NR_fstatat64:{
            int fd = va_arg(args,int);
            const char* path = va_arg(args,const char*);
            LOGD(TAG3,"fstatat64: %s",path);
            char*** ppChars = (char***)fstatat64_check_strs_array;
            while(*ppChars != nullptr){
                char** pChars = *ppChars;
                while(*pChars != nullptr){
                    char* str = *pChars;
                    if(strcmp(path,str) == 0) return -1;
                    ++pChars;
                }
                ++ppChars;
            }
            break;
        }



        default:break;
    }

    int result = ori_nms_syscall(cmd,stack[0],stack[1],stack[2],stack[3],stack[4],stack[5],stack[6]);
    return result;

}

int (*ori_nms_sysprop_read) (char* __name, char* __value);

int fake_nms_sysprop_read(char* name, char* value){
    LOGD(TAG4,"read property from nms: %s",name);
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

        //system.load("get loading...");
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
    DobbyHook((void*)(dlopen), (void*)fake_dlopen_1, (void**)&ori_dlopen);
    DobbyHook((void*)(system_property_get), (void*)fake_system_property_get, (void**)&ori_system_property_get);
   // DobbyHook((void*)(syscall), (void*)(fake_syscall), (void**)&ori_syscall);
}

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm,void* reserved){
    JNIEnv* env = NULL;
    vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6);

    doWork();

    return JNI_VERSION_1_6;
}