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
