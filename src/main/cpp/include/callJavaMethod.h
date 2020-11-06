//
// Created by Fear1ess on 2020/11/6.
//

#ifndef DEMO_CALLJAVAMETHOD_H
#define DEMO_CALLJAVAMETHOD_H

void saveJavaVM(JavaVM* vm);
jlong callLongMethod(jobject o,const char* methodName,const char* sig,...);


#endif //DEMO_CALLJAVAMETHOD_H
