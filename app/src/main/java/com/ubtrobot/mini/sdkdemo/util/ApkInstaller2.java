package com.ubtrobot.mini.sdkdemo.util;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.protobuf.StringValue;
import com.ubtrobot.master.Master;
import com.ubtrobot.master.param.ProtoParam;
import com.ubtrobot.master.service.ServiceProxy;
import com.ubtrobot.transport.message.CallException;
import com.ubtrobot.transport.message.Request;
import com.ubtrobot.transport.message.Response;
import com.ubtrobot.transport.message.ResponseCallback;
import com.ubtrobot.transport.message.StickyResponseCallback;

/**
 * 请使用这个工具进行APK安装
 */
public class ApkInstaller2 {

    private ApkInstaller2() {
    }

    private static class Holder {
        private static final ApkInstaller2 installer = new ApkInstaller2();
    }

    public static ApkInstaller2 get() {
        return Holder.installer;
    }

    public void installApk(@NonNull String apkPath, final Callback callback) {
        Master master = Master.get();
        ServiceProxy mSysEventProxy = master.getGlobalContext().createSystemServiceProxy("pkgService");
        mSysEventProxy.callStickily("/pkgservice/installApk", ProtoParam.create(StringValue.newBuilder().setValue(apkPath).build()), new StickyResponseCallback() {

            @Override
            public void onResponseStickily(Request request, Response response) {
                Log.i("installApk", "onResponseStickily: ");
            }

            @Override
            public void onResponseCompletely(Request request, Response response) {
                Log.i("installApk", "onResponseCompletely: ");
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            public void onFailure(Request request, CallException e) {
                Log.e("installApk", "onFailure: "+e.getMessage(), e);
                if (callback != null) {
                    callback.onFailure(e.getCode(), e.getMessage());
                }

            }
        });
    }

    public void uninstallApk(@NonNull String appId, final Callback callback) {
        Master master = Master.get();
        ServiceProxy mSysEventProxy = master.getGlobalContext().createSystemServiceProxy("pkgService");
        mSysEventProxy.call("/pkgservice/unInstallApk", ProtoParam.create(StringValue.newBuilder().setValue(appId).build()), new ResponseCallback() {
            public void onResponse(Request request, Response response) {
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            public void onFailure(Request request, CallException e) {
                if (callback != null) {
                    callback.onFailure(e.getCode(), e.getMessage());
                }
            }
        });
    }

    public interface Callback {
        void onSuccess();

        void onFailure(int var1, String var2);
    }
}
