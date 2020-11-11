//
// Created by Fear1ess on 2020/11/6.
//

#ifndef DEMO_CALLJAVAMETHOD_H
#define DEMO_CALLJAVAMETHOD_H

void saveJavaVM(JavaVM* vm);
jint callIntMethod(jobject o,const char* methodName,const char* sig,...);
jlong callLongMethod(jobject o,const char* methodName,const char* sig,...);
void callStringMethodAndGetChars(char* buf,jobject o,const char* methodName,const char* sig,...);

int getStringPropAndCopyToValue(char* value,const char* methodName);
int getIntPropAndCopyToValue(char* value,const char* methodName);


#endif //DEMO_CALLJAVAMETHOD_H
