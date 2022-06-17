package com.ubtrobot.mini.asrlib;

import android.util.Log;

import com.ubtechinc.codemao.CodeMaoSpeechRecognise;
import com.ubtrobot.master.Master;
import com.ubtrobot.master.param.ProtoParam;
import com.ubtrobot.master.service.ServiceProxy;
import com.ubtrobot.speech.asr.AsrRequest;
import com.ubtrobot.speech.asr.AsrUtils;
import com.ubtrobot.transport.message.CallException;
import com.ubtrobot.transport.message.Request;
import com.ubtrobot.transport.message.Response;
import com.ubtrobot.transport.message.StickyResponseCallback;


/**
 *
 */
public class AsrHelper {

    private ServiceProxy serviceProxy;

    private AsrHelper() {
    }

    private static class ServiceHolder {
        public static AsrHelper instance = new AsrHelper();
    }

    public static AsrHelper get() {
        return ServiceHolder.instance;
    }


    private ServiceProxy getCodeMaoServiceProxy() {
        if (serviceProxy == null) {
            serviceProxy = Master.get().getGlobalContext().createSystemServiceProxy("codemaoservice");
        }
        return serviceProxy;
    }

    /**
     * 开始机器人内置的 asr 识别
     *
     * @param timeLimit 录音时长，单位 ms，默认60000ms，即60s
     * @param callback
     */
    public void startSpeechRecognise(int timeLimit, AsrCallback callback) {
        getCodeMaoServiceProxy().callStickily("/codemao/speechRecogniseCall",
                ProtoParam.create(CodeMaoSpeechRecognise.SpeechRecogniseRequest.newBuilder()
                        .setTimeLimit(timeLimit)
                        .build()),
                new StickyResponseCallback() {
                    @Override
                    public void onResponseStickily(Request request, Response response) {
                        try {
                            CodeMaoSpeechRecognise.SpeechRecogniseResponse data = ProtoParam.from(response.getParam(),
                                    CodeMaoSpeechRecognise.SpeechRecogniseResponse.class).getProtoMessage();
//                            Log.i("startSpeechRecognise", "onResponseStickily: "+data);
                            if (null!=callback){
                                callback.onAsr(data.getText());
                            }
                        } catch (ProtoParam.InvalidProtoParamException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onResponseCompletely(Request request, Response response) {
                        try {
                            CodeMaoSpeechRecognise.SpeechRecogniseResponse data = ProtoParam.from(response.getParam(),
                                    CodeMaoSpeechRecognise.SpeechRecogniseResponse.class).getProtoMessage();
//                            Log.i("startSpeechRecognise", "onResponseCompletely: "+data);
                            if (null!=callback){
                                callback.onAsr(data.getText());
                                callback.onSuccess();
                            }
                        } catch (ProtoParam.InvalidProtoParamException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Request request, CallException e) {
                        Log.e("startSpeechRecognise", "onFailure: code = "+e.getCode() + " ,message = " + e.getMessage(), e);
                        if (null!=callback){
                            callback.onFailure(e);
                        }
                    }
                });
    }

    /**
     * 停止机器人内置的 asr 识别
     *
     * @param callback
     */
    public void stopSpeechRecognise(Callback callback) {
        getCodeMaoServiceProxy().callStickily("/codemao/stopSpeechRecogniseCall", new StickyResponseCallback() {
            @Override
            public void onResponseStickily(Request request, Response response) {

            }

            @Override
            public void onResponseCompletely(Request request, Response response) {
//                Log.i("stopSpeechRecognise", "onResponseCompletely: Success");
                if (null!=callback){
                    callback.onSuccess();
                }
            }

            @Override
            public void onFailure(Request request, CallException e) {
                Log.e("stopSpeechRecognise", "onFailure: code = "+e.getCode() + " ,message = " + e.getMessage(), e);
                if (null!=callback){
                    callback.onFailure(e);
                }
            }
        });
    }

    /* 以下的接口直接接收外部传入的PCM音频数据进行识别 */

    /**
     * step.1  调用开启识别, 开始一次识别会话 , 返回会话id
     *
     * @param sampleRate: 需要识别的音频的采样率枚举SampleRate,目前支持8K/16K,即Rate8K/Rate16K.
     * @param channel:    通道选择单声道(1)或者双声道(2)
     * @return
     */
    public long beginAsrSession(AsrRequest.SampleRate sampleRate, int channel) {
        return AsrUtils.getDefaultUploader().beginSession(sampleRate, channel);
    }

    /**
     * step.2  sendPCM数据
     *
     * @param data PCM格式音频数据
     */
    public void writePCM(byte[] data) {
        AsrUtils.getDefaultUploader().upload(data);
    }

    /**
     * step.3 结束本次识别会话，并返回识别结果
     */
    public String endAsrSession() {
        return AsrUtils.getDefaultUploader().endSession();
    }

    public interface Callback {
        void onSuccess();
        void onFailure(CallException e);
    }

    public interface AsrCallback extends Callback{
        void onAsr(String text);
    }
}
