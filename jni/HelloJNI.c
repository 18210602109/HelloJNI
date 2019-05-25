#include<stdio.h>
#include"jni.h"
#include"HelloJNI.h"

JNIEXPORT void JNICALL Java_HelloJNI_displayHelloJNI
  (JNIEnv *env, jobject obj)
{
    printf("this is a JNI test program!\n");
    return;
}
