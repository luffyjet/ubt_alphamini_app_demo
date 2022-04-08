package com.ubtrobot.mini.sdkdemo.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PhoneUtils {

    private static final String TAG = "PhoneUtils";

    /**
     * 拨打电话
     * @param phoneNumber
     */
    public static void call(String phoneNumber){
        Log.i(TAG, "call: "+phoneNumber);
        Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phoneNumber));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //do some thing,notify call start
        Utils.getContext().startActivity(intent);
    }

    /**
     * 接听来电
     */
    public static void answerCall(){
        Log.i(TAG, "answerCall: ");
        TelephonyManager telephonyService = (TelephonyManager) Utils.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        Method declaredMethod;
        try {
            if (telephonyService != null) {
                declaredMethod = telephonyService.getClass().getDeclaredMethod("getITelephony");
                declaredMethod.setAccessible(true);
                ITelephony itelephony = null;
                try {
                    itelephony = (ITelephony) declaredMethod.invoke(telephonyService);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                if (itelephony != null) {
                    try {
                        itelephony.answerRingingCall();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    Intent intent =new Intent();
                    intent.setAction("android.intent.action.CALL_BUTTON");
                    Utils.getContext().startActivity(intent);
                }
            }else {
                LogUtils.w(TAG, "getSystemService(Telephone) failure.");
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    /**
     * 挂断电话
     */
    public static void endCall(){
        Log.i(TAG, "endCall: ");
        TelephonyManager telephonyService = (TelephonyManager) Utils.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        Class<TelephonyManager> telephonyManagerClass = TelephonyManager.class;
        try {
            //得到TelephonyManager.getITelephony方法的Method对象
            Method method = telephonyManagerClass.getDeclaredMethod("getITelephony");
            //允许访问私有方法
            method.setAccessible(true);
            //调用getITelephony方法发挥ITelephony对象
            Object object = method.invoke(telephonyService);
            //挂断电话
            @SuppressLint("PrivateApi") Method endCall = Class.forName("com.android.internal.telephony.ITelephony").getMethod("endCall");
            Object result = endCall.invoke(object);
            LogUtils.i(TAG, "endCall = " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
