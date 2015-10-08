# Jni_SmartUpdate
Android app Smart Update use bspatch 4.3

Android智能升级
===========================================
使用bsdiff4.3-win32文件夹中的bsdiff.exe生成差异包

然后使用JniLib中的Utils类的patchApk方法 合并成新包安装
读取文件需要SD卡权限，否则可能失败

JNI接口文档 ：
http://docs.oracle.com/javase/7/docs/technotes/guides/jni/spec/jniTOC.html


JNI编译参见 Android.mk 文件
===========================================

patchApk方法说明
===========================================
bsspatch.c中有一个Utils.java中的native方法patchApk方法实现
java类生成.h文件的方法如下:

F:\JniLib\src\com\yxy\lib\smartupdate>javac Utils.java  //使用该命令需要在Utils.java所在的包之下，
//命令行显示为F:\JniLib\src\com\yxy\lib\smartupdate>

F:\JniLib\src>javah com.yxy.lib.smartupdate.Utils //生成com_yxy_lib_smartupdate_Utils.h文件，需要在src文件目录（命令行显示为F:\JniLib\src>）下调用该命令

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




