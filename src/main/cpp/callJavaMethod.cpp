//
// Created by Fear1ess on 2020/11/6.
//
#include <jni.h>
#include "include/callJavaMethod.h"

JavaVM* g_JVM = nullptr;

void saveJavaVM(JavaVM* vm){
    g_JVM = vm;
}

jlong callLongMethod(jobject o,const char* methodName,const char* sig,...){
    JNIEnv* env = nullptr;
    bool isAttached = true;
    int status = g_JVM->GetEnv((void**)&env,JNI_VERSION_1_6);
    if(status < 0){
        g_JVM->AttachCurrentThread(&env,NULL);
        isAttached = false;
    }
    jclass cls = env->GetObjectClass(o);
    jmethodID method = env->GetMethodID(cls,methodName,sig);
    env->DeleteLocalRef(cls);
    va_list args;
    va_start(args,sig);
    jlong result = env->CallLongMethodV(o,method,args);
    va_end(args);
    if(isAttached == false) g_JVM->DetachCurrentThread();
    return result;
}
