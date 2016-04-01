package com.lc.hp.share.httputils.impl;

import android.content.Context;

import com.lc.hp.share.httputils.BaseOkCall;
import com.lc.hp.share.httputils.MD5Util;
import com.lc.hp.share.httputils.UrlEncodeUtils;
import com.lc.hp.share.httputils.common.BaseParams;
import com.lc.hp.share.httputils.common.ProgressRequestBody;
import com.lc.hp.share.httputils.listener.DownCallback;
import com.lc.hp.share.httputils.listener.DownFileCall;
import com.lc.hp.share.httputils.listener.HttpCallback;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by hp on 2016/3/31.
 */
public class OkHttpImpl implements BaseHttpImpl {

    public OkHttpClient okHttpClient;
    public static OkHttpImpl instance;
    public static OkHttpImpl getInstance(OkHttpClient okHttpClient){
        if(instance==null){
            synchronized (OkHttpImpl.class){
                if(instance==null){
                    instance = new OkHttpImpl(okHttpClient);
                }
            }
        }else{
            if(okHttpClient !=null){
                instance.okHttpClient = okHttpClient;
            }
        }
        return instance;
    }

    public static OkHttpImpl getInstance(){
        if(instance==null){
            synchronized (OkHttpImpl.class){
                if(instance==null){
                    instance = new OkHttpImpl(null);
                }
            }
        }
        return instance;
    }

    public OkHttpImpl(OkHttpClient okHttpClient){
        if(okHttpClient!=null){
            this.okHttpClient = okHttpClient;
        }else{
            if(okHttpClient==null){
                this.okHttpClient = new OkHttpClient();
            }
        }
    }


    @Override
    public Call get(String url, BaseParams params, HttpCallback callback) {
        return get(false,url,params,callback,null,null);
    }



    @Override
    public Call get(boolean shouldEncodeUrl, String url, BaseParams params, HttpCallback callback, Object head, Object config) {
        Call call = null;
        try {
            Request request;
            if(params.tag!=null){
                 request = new Request.Builder()
                         .url(UrlEncodeUtils.getUrlWithQueryString(shouldEncodeUrl,url,params))
                         .tag(params.tag)
                         .get().build();
            }else{
                request = new Request.Builder()
                        .tag(MD5Util.md5(url))
                        .url(UrlEncodeUtils.getUrlWithQueryString(shouldEncodeUrl, url, params))
                        .get().build();
            }
            BaseOkCall handler = new BaseOkCall(callback,url,params);
            call = okHttpClient.newCall(request);
            call.enqueue(handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return call;
    }

    /**设置连接超时*/
    public void setConnectTimeout(int timeout,TimeUnit units){
        okHttpClient = okHttpClient.newBuilder().connectTimeout(timeout, units).build();
    }
   /**设置读取超时*/
    public void setReadTimeout(int timeout,TimeUnit units){
        okHttpClient = okHttpClient.newBuilder().readTimeout(timeout, units).build();
    }
    /**设置写入超时*/
    public void setWriteTimeout(int timeout,TimeUnit units){
        okHttpClient = okHttpClient.newBuilder().writeTimeout(timeout, units).build();
    }

    @Override
    public Call post(String url, BaseParams params, HttpCallback callback) {
        return post(url,params,callback,"");
    }

    @Override
    public Call post(String url, BaseParams params, HttpCallback callback, String mediatype) {
        return post(url,params,callback,mediatype,null);
    }

    @Override
    public Call post(String url, BaseParams params, HttpCallback callback, String mediatype, Object head) {
        RequestBody requestBody = null;
        String type ="";
        MediaType mediaType = null;
        if(!mediatype.trim().equals(""))
            mediaType = MediaType.parse(mediatype);
        if(mediaType!=null)
            type = mediaType.type()+"/"+mediaType.subtype();
        switch (type){
            case "text/plain":
                requestBody = RequestBody.create(mediaType,params.strParams.get("apple_txt"));
                break;
            case "application/octet-stream":
                break;
            default:
                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                for(ConcurrentHashMap.Entry<String,String> entry : params.strParams.entrySet()){
                    builder.addPart(Headers.of("Content-Disposition","form-data;name=\""+entry.getKey()+"\""),RequestBody.create(null,entry.getValue()));
                }
                if(params.fileParams.size()>0){
                    for(ConcurrentHashMap.Entry<String,BaseParams.FileWrapper> entry :params.fileParams.entrySet()){
                        File file = entry.getValue().file;
                        String fileName = file.getName();
                        RequestBody fileBody = RequestBody.create(MediaType.parse(UrlEncodeUtils.guessMimeType(fileName)),file);
                        builder.addFormDataPart(entry.getKey(),fileName,fileBody);
                    }
                }
                requestBody = builder.build();
                break;
        }
        Call call = null;
        Request request;
        if(params.tag!=null){
            request = new Request.Builder()
                    .url(url)
                    .tag(params.tag)
                    .post(new ProgressRequestBody(requestBody,callback))
                    .build();
        }else{
            request = new Request.Builder()
                    .url(url)
                    .post(new ProgressRequestBody(requestBody,callback))
                    .build();
        }

        BaseOkCall handler = new BaseOkCall(callback,url,null);
        call =  okHttpClient.newCall(request);
        call.enqueue(handler);
        return call;
    }
    @Override
    public Call downloadFile(Context context, String url, DownCallback callback, BaseParams params, String destFileDir, String destFileNam) {
        Call call = null;
        Request request ;
        if(params.tag!=null){
            request = new Request.Builder()
                    .url(UrlEncodeUtils.getUrlWithQueryString(true,url,params))
                    .tag(params.tag)
                    .get().build();
        }else{
            request = new Request.Builder()
                    .tag(MD5Util.md5(url))
                    .url(UrlEncodeUtils.getUrlWithQueryString(true, url, params))
                    .get().build();
        }
        DownFileCall handler = new DownFileCall(context,callback,url,destFileDir,destFileNam);
        call = okHttpClient.newCall(request);
        call.enqueue(handler);
        return call;
    }
}
