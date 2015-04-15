package com.yxy.lib.smartupdate;

public class Utils {
	static{
		System.loadLibrary("bspatch");
	}
	
	public static native int patchApk(String oldPath,String newPath,String patchPath);
}
