# Jni_SmartUpdate
Android app Smart Update use bspatch 4.3

Android智能升级
===========================================
使用bsdiff4.3-win32文件夹中的bsdiff.exe生成差异包

然后使用JniLib中的Utils类的patchApk方法 合并成新包安装
读取文件需要SD卡权限，否则可能失败

JNI接口文档 ：
http://docs.oracle.com/javase/7/docs/technotes/guides/jni/spec/jniTOC.html


应用场景：
=========================================================

服务器发布新版本v1.1，客服端当前版本为v1.0，使用bsdiff生成差分包patch_v1.0_v1.1，
然后客户端下载patch包到本地然后与本地的v1.0.apk合并生成v1.1.apk，然后执行安装，升级完成

增量升级可以优化升级速度，修复紧急BUG之类的，比如源APK大小10M，增量升级patch包可能就1-2M或者几百K，可以实现快速升级

 实现：
=========================================================

使用工具为bsdiff、bspatch来完成差分增量包和合并增量包，下载地址为：http://www.daemonology.net/bsdiff/ 

win32版本使用命令如：
bsdiff.exe oldfile newfile patchfile
bspatch.exe oldfile newfile patchfile
android中的实现则需要使用JNI调用c文件来完成
1.首先新建一个Utils类:
=========================================================
```
	package com.yxy.lib.smartupdate;
	
	public class Utils {
		static{
			System.loadLibrary("bspatch");
		}
		
		public static native int patchApk(String oldPath,String newPath,String patchPath);
	}
```
2.使用java编译生成.h文件的方法
=========================================================

//编译java文件
```
	F:\JniLib\src\com\yxy\lib\smartupdate>javac Utils.java
```
//生成后的.h文件名格式为 包名_类名.h >> com_yxy_lib_smartupdate_Utils.h
```
	 F:\JniLib\src>javah com.yxy.lib.smartupdate.Utils
```
3.使用c++实现底层方法
=========================================================
在项目根目录下建立jni文件夹，放入bspatch.c相关的c文件实现，可以直接拷贝底部demo中的jni文件夹内的文件，替换掉方法即可

生成后的.h文件中native方法名称格式为java_包名_类名_方法名

com_yxy_lib_smartupdate_Utils.h文件可以在bspatch.c文件中使用#include "com_yxy_lib_smartupdate_Utils.h"实现， 也可以直接复制方法到bspatch.c中实现，本demo中复制该方法到bspatch.c完成实现：
```
	JNIEXPORT jint JNICALL Java_com_yxy_lib_smartupdate_Utils_patchApk(JNIEnv *env,
	        jobject obj, jstring old, jstring new , jstring patch){
	  //构建4个长度的数组
	  int argc=4;
	  char * argv[argc];
	  argv[0]="fail.";
	  argv[1]=(*env)->GetStringUTFChars(env,old, 0);
	  argv[2]=(*env)->GetStringUTFChars(env,new, 0);
	  argv[3]=(*env)->GetStringUTFChars(env,patch, 0);
	  __android_log_print(ANDROID_LOG_INFO, "JNIMsg", "oldFilePath = %s",argv[1]);
	  __android_log_print(ANDROID_LOG_INFO, "JNIMsg", "newFilePath = %s",argv[2]);
	  __android_log_print(ANDROID_LOG_INFO, "JNIMsg", "patchFilePath = %s",argv[3]);
	  int ret=applypatch(argc, argv);
	   (*env)->ReleaseStringUTFChars(env,old,argv[1]);
	   (*env)->ReleaseStringUTFChars(env,new,argv[2]);
	   (*env)->ReleaseStringUTFChars(env,patch,argv[3]);
	   return ret;
	}
	__android_log_print 需要导入 #include <android/log.h>
	如果构建的时候报JNIEXPORT找不到，则需要在bspatch.c中#include <jni.h>
```

4.配置android.mk 
=========================================================

JNI编译参数参见 Android.mk 文件

```
	# Copyright (C) 2009 The Android Open Source Project
	#
	# Licensed under the Apache License, Version 2.0 (the "License");
	# you may not use this file except in compliance with the License.
	# You may obtain a copy of the License at
	#
	# http://www.apache.org/licenses/LICENSE-2.0
	#
	# Unless required by applicable law or agreed to in writing, software
	# distributed under the License is distributed on an "AS IS" BASIS,
	# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	# See the License for the specific language governing permissions and
	# limitations under the License.
	#
	LOCAL_PATH := $(call my-dir)
	#构建一个静态库，bspatch中需要调用libbz.a和bzlib.h中的函数或参数，需要先构建
	#building static library
	include $(CLEAR_VARS)
	LOCAL_MODULE:= libbz
	LOCAL_SRC_FILES:= libbz.a
	LOCAL_EXPORT_C_INCLUDES := bzlib.h
	include $(PREBUILT_STATIC_LIBRARY)
	include $(CLEAR_VARS)
	#
	#构建so文件
	# build so lib
	#so文件名，构建完成会自动将libbspatch.so放在libs\armeabi文件夹之下
	LOCAL_MODULE:= libbspatch
	# All of the source files that we will compile.
	#编译文件夹
	LOCAL_SRC_FILES:= \
	bspatch.c \
	bsdiff.c
	# Also need the JNI headers.
	#导入bzlib文件
	LOCAL_EXPORT_C_INCLUDES := bzlib.h
	# No static libraries.
	#导入上面构建静态库
	LOCAL_STATIC_LIBRARIES := \
	libbz
	LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog
	# No special compiler flags.
	LOCAL_CFLAGS +=
	#构建共享库
	#build so file
	include $(BUILD_SHARED_LIBRARY)
```
5.生成SO文件
=========================================================

命令行进入项目的根目录，使用ndk-build命令生成so文件，如果未安装ndk请先到官网下载ndk

