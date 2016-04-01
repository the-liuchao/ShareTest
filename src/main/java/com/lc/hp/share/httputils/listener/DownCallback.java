package com.lc.hp.share.httputils.listener;

import com.lc.hp.share.httputils.entity.DownEntity;

/**
 * Created by hp on 2016/4/1.
 */
public interface DownCallback {
    /**
     * 下载返回实体类
     * @param entity
     */
    void onProgress(DownEntity entity);
}
