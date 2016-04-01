package com.lc.hp.share.httputils.common;

import android.content.Context;

import com.lc.hp.share.httputils.MD5Util;
import com.lc.hp.share.httputils.impl.OkHttpImpl;
import com.lc.hp.share.httputils.listener.DownCallback;
import com.lc.hp.share.httputils.listener.HttpCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;

/**
 * Created by hp on 2016/3/31.
 */
public class BaseHttpCall {

    static BaseHttpCall baseCall = null;
    BaseParams mParams;

    private String mediaType;

    String destFileDir;
    String destFileName;

    String url = "";

    private BaseHttpCall(){
        initParameter();
        url = "";
    }

    /**
     * 单例模式
     */
    public static BaseHttpCall getBaseCall() {
        if (baseCall == null)
            baseCall = new BaseHttpCall();
        return baseCall;
    }

    public BaseHttpCall setBaseParams(BaseParams baseParams) {
        if (baseParams != null) {
            initParameter();
            mParams = baseParams;
        }
        return this;
    }

    /**
     * 初始化参数
     */
    private void initParameter() {
        if (mParams == null)
            mParams = new BaseParams();
        else
            mParams.clear();
    }

    /**
     * 设置网络请求地址
     */
    public BaseHttpCall setUrl(String requestUrl) {
        if (url != null)
            url = requestUrl;
        return this;
    }

    public BaseHttpCall put(String key, long value) {
        if (key != null)
            mParams.put(key, String.valueOf(value));
        return this;
    }

    public BaseHttpCall put(String key, ArrayList<File> file) throws FileNotFoundException {
        if (key != null && file != null)
            mParams.put(key, file, null, null);
        return this;
    }

    public BaseHttpCall put(String key, File file) throws FileNotFoundException {
        if (key != null && file != null)
            mParams.put(key, file, null, null);
        return this;
    }

    public BaseHttpCall put(String key, String customFileName, File file) throws FileNotFoundException {
        mParams.put(key, file, null, customFileName);
        return this;
    }

    public BaseHttpCall put(String key, int value) {
        if (key != null)
            mParams.put(key, String.valueOf(value));
        return this;
    }

    public BaseHttpCall put(String key, String value) {
        if (key != null)
            mParams.put(key, value);
        return this;
    }

   /**普通get请求*/
    public BaseHttpCall getRequest(HttpCallback callback){
        OkHttpImpl.getInstance().get(url,mParams,callback);
        return this;
    }

    /**普通post请求*/
    public BaseHttpCall postRequest(HttpCallback callback){
        OkHttpImpl.getInstance().post(url, mParams, callback);
        return this;
    }
    /**普通post提交文本数据*/
    public BaseHttpCall postStringRequest(HttpCallback callback){
        OkHttpImpl.getInstance().post(url, mParams, callback, "application/json");
        return this;
    }
   /**post文件上传*/
    public BaseHttpCall postFileRequest(HttpCallback callback){
        OkHttpImpl.getInstance().post(url,mParams,callback);
        return this;
    }
    /**get文件下载*/
    public BaseHttpCall downlaodFile(Context context,DownCallback callback){
        OkHttpImpl.getInstance().downloadFile(context, url, callback, mParams, destFileDir, destFileName);
        return this;
    }
   /**设置文件下载保存目录*/
    public BaseHttpCall downDir(String dir){
        this.destFileDir = dir;
        return this;
    }

    /**设置文件下载保存文件名*/
    public BaseHttpCall downName(String fileName){
        this.destFileName = fileName;
        return this;
    }
    /**设置网路连接超时*/
    public BaseHttpCall setConnectTimeout(int timeout,TimeUnit units){
        OkHttpImpl.getInstance().setConnectTimeout(timeout,units);
        return this;
    }
    /**设置网络读取超时*/
    public BaseHttpCall setReadTimeout(int timeout,TimeUnit units){
        OkHttpImpl.getInstance().setReadTimeout(timeout, units);
        return this;
    }
    /**设置网络写超时*/
    public BaseHttpCall setWriteTimeout(int timeout,TimeUnit units){
        OkHttpImpl.getInstance().setWriteTimeout(timeout, units);
        return this;
    }

    /***
     * 设置tag操作
     * @param tag
     * @return
     */
    public BaseHttpCall setTag(Object tag){
        mParams.setTag(tag);
        return this;
    }
   /**传入tag以便关闭线程*/
    public Call sendGetRequest(String url,BaseParams params,HttpCallback callback){
        return OkHttpImpl.getInstance().get(false,url,params,callback,null,null);
    }

    public Call sendPostRequest(String url,BaseParams params,HttpCallback callback){
        return OkHttpImpl.getInstance().post(url, params, callback);
    }

    public Call sendGetRequest(boolean shouldEncodeUrl,String url,BaseParams params,HttpCallback callback, Headers headers){
        return OkHttpImpl.getInstance().get(shouldEncodeUrl, url, params, callback, "", headers);
    }
     /**返回网络请求对象*/
    public OkHttpClient getOKHttpClient(OkHttpClient client){
        return OkHttpImpl.getInstance().okHttpClient;
    }
   /**关闭网络库可以通过url来关闭*/
    public void cancel(String url){
        try {
            for (Call call : OkHttpImpl.getInstance().okHttpClient.dispatcher().queuedCalls()){
                if(MD5Util.md5(url).equals(call.request().tag())){
                    call.cancel();
                }
            }
            for(Call call : OkHttpImpl.getInstance().okHttpClient.dispatcher().runningCalls()){
                if(MD5Util.md5(url).equals(call.request().tag())){
                    call.cancel();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**关闭网络库可以通过tag来关闭*/
    public void cancelTag(Object object){
        try {
            for (Call call : OkHttpImpl.getInstance().okHttpClient.dispatcher().queuedCalls()){
                if(object.equals(call.request().tag())){
                    call.cancel();
                }
            }
            for(Call call : OkHttpImpl.getInstance().okHttpClient.dispatcher().runningCalls()){
                if(object.equals(call.request().tag())){
                    call.cancel();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
