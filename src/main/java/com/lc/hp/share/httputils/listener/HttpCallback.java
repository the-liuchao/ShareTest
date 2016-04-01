package com.lc.hp.share.httputils.listener;

/**
 * Created by hp on 2016/3/31.
 */
public interface HttpCallback {
    /**
     * 请求成功
     *
     * @param content 返回值
     * @param object  返回转化对象
     * @param reqType 请求唯一识别
     */
    void onSuccess(String content, Object object, String reqType);

    /**
     * 请求失败
     * @param error 错误
     * @param content 返回值
     * @param reqType 请求的唯一表示
     */
    void onFailure(Throwable error,String content,String reqType);

    void onProgress(long bytesRead,long contentLength,boolean done);
}
