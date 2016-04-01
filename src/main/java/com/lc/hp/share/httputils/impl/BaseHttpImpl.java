package com.lc.hp.share.httputils.impl;

import android.content.Context;

import com.lc.hp.share.httputils.common.BaseParams;
import com.lc.hp.share.httputils.listener.DownCallback;
import com.lc.hp.share.httputils.listener.HttpCallback;

/**
 * Created by hp on 2016/4/1.
 */
public interface BaseHttpImpl {

    /**网络库接口定义*/
    Object get(String url,BaseParams params,HttpCallback callback);
    Object downloadFile(Context context,String url,DownCallback callback,BaseParams params,String destFileDir, String destFileNam);

    Object get(boolean shouldEncodeUrl,String url,BaseParams params,HttpCallback callback,Object head,Object config);

    Object post(String url, BaseParams params, HttpCallback callback);
    Object post(String url, BaseParams params, HttpCallback callback,String mediatype);
    Object post(String url, BaseParams params, HttpCallback callback,String mediatype,Object head);

}