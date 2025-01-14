package com.ubtrobot.mini.sdkdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import com.ubtrobot.mini.sdkdemo.util.ApkInstaller2;

public class ApkSilentInstallerApiActivity extends Activity {

  private static final String TAG = "ApkSilentInstaller";

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.apk_silent_installer_api);
  }

  public void installApk(View view) {
    ApkInstaller2.get().installApk("/sdcard/xxx.apk", new ApkInstaller2.Callback() {
      @Override public void onSuccess() {
        Log.d(TAG, "install success.");
      }

      @Override public void onFailure(int code, String errmsg) {
        Log.d(TAG, "install failure, code = " + code + ", msg = " + errmsg);
      }
    });
  }

  public void uninstallApk(View view) {
    ApkInstaller2.get().uninstallApk("com.xxx.xxx", new ApkInstaller2.Callback() {
      @Override public void onSuccess() {
        Log.d(TAG, "uninstall success.");
      }

      @Override public void onFailure(int code, String errmsg) {
        Log.d(TAG, "uninstall failure, code = " + code + ", msg = " + errmsg);
      }
    });
  }
}
