linux下JNI实现步骤
1、编写java文件，如下(HelloJNI.java)：
	public class HelloJNI{
	    public native void displayHelloJNI();//使用native关键将displayHelloJNI声明为本地方法
	    
	    static{
		System.loadLibrary("MyLib");//加载so库
	    }

	    public static void main(String[] args){
		new HelloJNI().displayHelloJNI();	//接口测试
	    }
	}


	执行javac HelloJNI.java命令，生成HelloJNI.class
2、生成java接口文件HelloJNI.h
	执行：javah -jni HelloJNI, 生成HelloJNI.h文件

	文件内容：
	#include <jni.h>
	/* Header for class HelloJNI */

	#ifndef _Included_HelloJNI
	#define _Included_HelloJNI
	#ifdef __cplusplus
	extern "C" {
	#endif
	/*
	 * Class:     HelloJNI
	 * Method:    displayHelloJNI
	 * Signature: ()V
	 */
	JNIEXPORT void JNICALL Java_HelloJNI_displayHelloJNI
	  (JNIEnv *, jobject);

	#ifdef __cplusplus
	}
	#endif
	#endif

3、编写c程序(HelloJNI.c)
	文件内容：
	#include<stdio.h>
	#include"jni.h"
	#include"HelloJNI.h"

	JNIEXPORT void JNICALL Java_HelloJNI_displayHelloJNI
	  (JNIEnv *env, jobject obj)
	{
	    printf("this is a JNI test program!\n");
	    return;
	}

4.1、编写Makefile文件(用于java环境调用)
	文件内容：
	libMyLib.so:HelloJNI.c
		gcc -shared -I /usr/lib/jvm/java-8-openjdk-i386/include -I /usr/lib/jvm/java-8-openjdk-i386/include/linux HelloJNI.c -o libMyLib.so

	clean:
	rm -f libMyLib.so

	执行make命令，生成libMyLib.so库， 这个文件哟用于java程序调用

	-I 参数包jni.h文件路径，
	查看jdk安装路径：
	1)which java, 输出：/usr/bin/java
	2)ls -lrt /usr/bin/java, 输出：/usr/bin/java -> /etc/alternatives/java
	3)ls -lrt /etc/alternatives/java， 输出：/etc/alternatives/java -> /usr/lib/jvm/jre-1.7.0-openjdk.x86_64/bin/java

4.2、编写Android.mk 文件(用于android环境调用)
	文件内容：
	LOCAL_PATH := $(call my-dir)

	include $(CLEAR_VARS)
	LOCAL_ALLOW_UNDEFINED_SYMBOLS := true
	LOCAL_MODULE    :=HelloJNI 
	#LOCAL_C_INCLUDES += /usr/include
	LOCAL_C_INCLUDES += /root/android-ndk-r10/samples/HelloJNI
	LOCAL_SRC_FILES := HelloJNI.c 
	LOCAL_LDLIBS    := -llog
	#LOCAL_WHOLE_STATIC_LIBRARIES += android_support
	#LOCAL_CFLAGS += -D_GNU_SOURCE
	include $(BUILD_SHARED_LIBRARY)
	#$(call import-module, android/support)


5、java测试接口：
	执行java HelloJNI命令，提示Exception in thread "main" java.lang.UnsatisfiedLinkError: no MyLib in java.library.path(错误提示找不着Mylib库文件)
	执行java -Djava.library.path=. HelloJNI命令，输出：this is a JNI test program!
	或者
	执行：
	export LD_LIBRARY_PATH=/home/du/test_code:$LD_LIBRARY_PATH	//设置Mylib库路径环境变量
	java HelloJNI
