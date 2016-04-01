package com.lc.hp.share.httputils.common;

import com.lc.hp.share.httputils.listener.HttpCallback;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by hp on 2016/4/1.
 */
public class ProgressRequestBody extends RequestBody{

    private final RequestBody requestBody;//实际的待包装请求体

    private final HttpCallback progressListener;//进度回调接口

    private  BufferedSink bufferedSink;//包装完成的BufferedSink

    public ProgressRequestBody(RequestBody requestBody, HttpCallback progressListener) {
        this.requestBody = requestBody;
        this.progressListener = progressListener;
    }

    /**重写实际响应体contentType*/
    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    /**重写调用实际响应体的contentLength*/
    @Override
    public long contentLength() throws IOException {
        return super.contentLength();
    }

    /**重新写入*/
    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if(bufferedSink==null) {
            //包装
            bufferedSink = Okio.buffer(sink(sink));
        }
        requestBody.writeTo(bufferedSink);
        //必须调用flush，否则最后一部分数据可能不会被写入
        bufferedSink.flush();
    }

    /**写入，回调进度接口*/
    private Sink sink(Sink sink){
        return new ForwardingSink(sink) {
            //当前写入的字节数
            long bytesWritten = 0l;
            //总字节长度,避免多次调用contentLength()方法
            long contentLength = 0l;
            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if(contentLength==0){
                    //获取contentLength的值，后续不再调用
                    contentLength = contentLength();
                }
                //增加当前写入的字节数
                bytesWritten += byteCount;
                if(progressListener!=null)
                    progressListener.onProgress(bytesWritten,contentLength,bytesWritten==contentLength);
            }
        };
    }
}
