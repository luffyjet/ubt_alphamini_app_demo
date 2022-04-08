package com.ubtrobot.mini.sdkdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ubtrobot.mini.sdkdemo.util.PhoneUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * PhoneCall usage for Oversea custom version robot
 */
public class PhoneCallApi2Activity extends Activity   {
    private static final String TAG = "PhoneCallApi2";

    private EditText mTelno;
    private Button mCalltel;
    private Button mAnswer;
    private Button mHangUp;

    private ExecutorService executor = Executors.newFixedThreadPool(2);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonecall2);
        initView();
    }

    private void initView() {
        mTelno = findViewById(R.id.telno);
        mCalltel = findViewById(R.id.calltel);
        mAnswer = findViewById(R.id.answer);
        mHangUp = findViewById(R.id.hang_up);

        mCalltel.setOnClickListener(mClickListener);
        mAnswer.setOnClickListener(mClickListener);
        mHangUp.setOnClickListener(mClickListener);
    }


    private final View.OnClickListener mClickListener = v -> {
        switch (v.getId()) {
            case R.id.calltel:
                makeCall();
                break;
            case R.id.answer://answer a call
                executor.execute(() -> {
                    PhoneUtils.answerCall();
                    Log.d(TAG,"answer execute");
                });
                break;
            case R.id.hang_up://hang up a call
                executor.execute(() -> {
                    PhoneUtils.endCall();
                    Log.d(TAG,"hangup execute");
                });
                break;
            default:
                break;
        }
    };


    private void makeCall() {
        String phoneNumber = mTelno.getText().toString().trim();

        if(TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(getApplicationContext(),"phone number is null!",Toast.LENGTH_LONG).show();
            return;
        }

        PhoneUtils.call(phoneNumber);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
