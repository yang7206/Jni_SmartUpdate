# Jni_SmartUpdate
Android app Smart Update use bspatch 4.3

Android智能升级
===========================================
使用bsdiff4.3-win32文件夹中的bsdiff.exe生成差异包

然后使用JniLib中的Utils类的patchApk方法 合并成新包安装
读取文件需要SD卡权限，否则可能失败

JNI接口文档 ：
http://docs.oracle.com/javase/7/docs/technotes/guides/jni/spec/jniTOC.html


Android.mk 文件说明:
===========================================
>	
# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PATH := $(call my-dir)
#
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
#编译文件夹
# All of the source files that we will compile.  
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

patchApk方法说明
===========================================
bsspatch.c中有一个Utils.java中的native方法patchApk方法实现
java类生成.h文件的方法如下:

javac Utils.java  //使用该命令需要在Utils.java所在的文件夹之下，
//命令行显示为F:\JniLib\src\com\yxy\lib\smartupdate>

javah com.yxy.lib.smartupdate.Utils //生成com_yxy_lib_smartupdate_Utils.h文件，需要在src文件目录（命令行显示为F:\JniLib\src>）下调用该命令

com_yxy_lib_smartupdate_Utils.h文件可以在bspatch.c文件中#include "com_yxy_lib_smartupdate_Utils.h" 实现，
也可以直接复制方法到bspatch.c中实现

如果构建的时候报JNIEXPORT找不到，则需要#include <jni.h>

JNIEXPORT jint JNICALL Java_com_yxy_lib_smartupdate_Utils_patchApk(JNIEnv *env,
        jobject obj, jstring old, jstring new , jstring patch){
  构建4个长度的数组
  int argc=4;
  char * argv[argc];
  argv[0]="fail.";
  argv[1]=(*env)->GetStringUTFChars(env,old, 0);
  argv[2]=(*env)->GetStringUTFChars(env,new, 0);
  argv[3]=(*env)->GetStringUTFChars(env,patch, 0);

	__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "oldFilePath = %s",
			argv[1]);
	__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "newFilePath = %s",
			argv[2]);
	__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "patchFilePath = %s",
			argv[3]);

  int ret=applypatch(argc, argv);

   (*env)->ReleaseStringUTFChars(env,old,argv[1]);
   (*env)->ReleaseStringUTFChars(env,new,argv[2]);
   (*env)->ReleaseStringUTFChars(env,patch,argv[3]);
   return ret;
}




