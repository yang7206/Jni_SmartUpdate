package com.example.jnidemo;

import java.io.File;
import java.io.IOException;

import com.yxy.lib.smartupdate.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String oldFilePath = Environment.getExternalStorageDirectory() + File.separator + "oldreader.apk";
				String patchPath = Environment.getExternalStorageDirectory() + File.separator + "updatepatch";
				String newFilePath = Environment.getExternalStorageDirectory() + File.separator + "newreader.apk";
//				File f = new File(newFilePath);
//				if(f.exists()){
//					f.delete();
//				}
//				if (!f.exists()) {
//					try {
//						boolean create = f.createNewFile();
//						System.out.println("create :" + create);
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
				Utils.patchApk(oldFilePath, newFilePath, patchPath);
			}
		});
		findViewById(R.id.btn1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String oldFilePath = Environment.getExternalStorageDirectory() + File.separator + "BBQProject.apk";
				String patchPath = Environment.getExternalStorageDirectory() + File.separator + "patch";
				String newFilePath1 = Environment.getExternalStorageDirectory() + File.separator + "E_Runner1.apk";
			}
		});
	}

}
