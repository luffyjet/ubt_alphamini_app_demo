package com.ubtrobot.mini.sdkdemo.api;

import android.support.annotation.Nullable;

import com.ubtrobot.master.Master;
import com.ubtrobot.master.call.CallConfiguration;
import com.ubtrobot.master.service.ServiceProxy;
import com.ubtrobot.transport.message.ResponseCallback;

public class SmallActionExtApi {
    private static final String TAG = "SmallActionApi";
    private final Master master;
    final ServiceProxy speech;

    private SmallActionExtApi() {
        this.master = Master.get();
        this.speech = this.master.getGlobalContext().createSystemServiceProxy("small_action");
        CallConfiguration configuration = (new CallConfiguration.Builder()).suppressSyncCallOnMainThreadWarning(true).build();
        this.speech.setConfiguration(configuration);
    }

    public static SmallActionExtApi get() {
        return Holder._api;
    }

    public void start(@Nullable final ResponseCallback listener) {
        this.speech.call("/small/execute/random", null, listener);
    }

    public void stop(@Nullable final ResponseCallback listener) {
        this.speech.call("/small/stop", null, listener);
    }

    private static final class Holder {
        private static SmallActionExtApi _api = new SmallActionExtApi();

        private Holder() {
        }
    }


}
