package com.lc.hp.share;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lc.hp.share.httputils.common.BaseHttpCall;
import com.lc.hp.share.httputils.common.BaseParams;
import com.lc.hp.share.httputils.listener.HttpCallback;

public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BaseHttpCall.getBaseCall()
                .setUrl("http://www.baidu.com")
                .setBaseParams(new BaseParams())
                .getRequest(new HttpCallback() {
                    @Override
                    public void onSuccess(String content, Object object, String reqType) {
                        Log.e("QQ:295476281", "reuslt = " + content);
                    }

                    @Override
                    public void onFailure(Throwable error, String content, String reqType) {
                        Log.e("QQ:295476281", "error = " + error.getStackTrace());
                    }

                    @Override
                    public void onProgress(long bytesRead, long contentLength, boolean done) {

                    }
                });
    }
}
