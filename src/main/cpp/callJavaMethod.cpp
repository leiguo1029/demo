//
// Created by Fear1ess on 2020/11/6.
//
#include <jni.h>
#include <cstring>
#include <cstdio>
#include "include/callJavaMethod.h"

extern jobject g_DIM;

JavaVM* g_JVM = nullptr;

void saveJavaVM(JavaVM* vm){
    g_JVM = vm;
}

jlong callLongMethod(jobject o,const char* methodName,const char* sig,...){
    JNIEnv* env = nullptr;
    bool needDetach = false;
    int status = g_JVM->GetEnv((void**)&env,JNI_VERSION_1_6);
    if(status < 0){
        g_JVM->AttachCurrentThread(&env,NULL);
        needDetach = true;
    }
    jclass cls = env->GetObjectClass(o);
    jmethodID method = env->GetMethodID(cls,methodName,sig);
    env->DeleteLocalRef(cls);
    va_list args;
    va_start(args,sig);
    jlong result = env->CallLongMethodV(o,method,args);
    va_end(args);
    if(needDetach) g_JVM->DetachCurrentThread();
    return result;
}


jint callIntMethod(jobject o,const char* methodName,const char* sig,...){
    JNIEnv* env = nullptr;
    bool needDetach = false;
    int status = g_JVM->GetEnv((void**)&env,JNI_VERSION_1_6);
    if(status < 0){
        g_JVM->AttachCurrentThread(&env,NULL);
        needDetach = true;
    }
    jclass cls = env->GetObjectClass(o);
    jmethodID method = env->GetMethodID(cls,methodName,sig);
    env->DeleteLocalRef(cls);
    va_list args;
    va_start(args,sig);
    jlong result = env->CallIntMethodV(o,method,args);
    va_end(args);
    if(needDetach) g_JVM->DetachCurrentThread();
    return result;
}


void callStringMethodAndGetChars(char* buf,jobject o,const char* methodName,const char* sig,...){
    JNIEnv* env = nullptr;
    bool needDetach = false;
    int status = g_JVM->GetEnv((void**)&env,JNI_VERSION_1_6);
    if(status < 0){
        g_JVM->AttachCurrentThread(&env,NULL);
        needDetach = true;
    }
    jclass cls = env->GetObjectClass(o);
    jmethodID method = env->GetMethodID(cls,methodName,sig);
    env->DeleteLocalRef(cls);
    va_list args;
    va_start(args,sig);
    jstring result = (jstring)env->CallObjectMethodV(o,method,args);
    va_end(args);
    jboolean isCopy;
    const char* cstr = env->GetStringUTFChars(result,&isCopy);
    int len = env->GetStringLength(result);
    memcpy(buf,cstr,len);
    env->ReleaseStringUTFChars(result,cstr);
    env->DeleteLocalRef(result);
    if(needDetach) g_JVM->DetachCurrentThread();
}

int getStringPropAndCopyToValue(char* value,const char* methodName){
    char str[128] = {0};
    callStringMethodAndGetChars(str,g_DIM,methodName,"()Ljava/lang/String;");
    memcpy(value,str,strlen(str)+1);
    return strlen(str);
}

int getIntPropAndCopyToValue(char* value,const char* methodName){
    int intVal = callIntMethod(g_DIM,methodName,"()I");
    char int_str[20] = {0};
    int len = sprintf(int_str,"%d",intVal);
    memcpy(value,int_str,len);
    return len;
}
