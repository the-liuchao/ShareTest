package com.lc.hp.share.httputils.listener;

import android.content.Context;
import android.util.Log;

import com.lc.hp.share.httputils.MD5Util;
import com.lc.hp.share.httputils.StorageUtils;
import com.lc.hp.share.httputils.entity.DownEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by hp on 2016/4/1.
 */
public class DownFileCall implements Callback {

    DownCallback callback;//自定请求返回对象
    String url;
    private String destFileDir;//目标文件存储文件夹路径
    private String destFileName;//目标文件存储的文件名
    Context context;

    DownEntity downEntity;

    public DownFileCall(Context context, DownCallback callback, String requestUrl, String destFileDir, String destFileName) {
        this.callback = callback;
        this.url = requestUrl;
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
        this.context = context;
        downEntity = new DownEntity();
        downEntity.downUrl(url);
        if (destFileDir != null && destFileDir.trim().toString().equals(""))
            downEntity.downDir(destFileDir);
        if (destFileName != null && destFileName.trim().toString().equals(""))
            downEntity.downName(destFileName);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        Log.i("295476281", "handle==onFailure=REQ_GET_INIT=reqType=");
        downEntity.statue = false;
        downEntity.isCanceled = call.isCanceled();
        downEntity.isExecuted = call.isExecuted();
        downEntity.downMessage(e.toString());
        callback.onProgress(downEntity);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        Log.i("295476281", "handle==onResponse=reqType=");
        if (response.isSuccessful()) {
            downEntity.downStatue(true);
            downEntity.downCode(response.code());
            downEntity.downMessage(response.body().toString());
            saveFile(response);
        } else {
            downEntity.downStatue(false);
            downEntity.downCode(response.code());
            downEntity.downMessage(response.body().toString());
            callback.onProgress(downEntity);
        }
    }

    /***
     * 文件下载保存
     */
    private File saveFile(Response response) {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            final long total = response.body().contentLength();
            long sum = 0;
            File dir;
            if (destFileDir == null || destFileDir.trim().toString().equals("")) {
                destFileDir = StorageUtils.getCacheDirectory(context).getAbsolutePath();
            }
            dir = new File(destFileDir);
            if (!dir.exists())
                dir.mkdirs();
            if (destFileName == null || destFileName.trim().toString().equals("")) {
                destFileName = MD5Util.md5(url);
            }
            File file = new File(dir, destFileName + "." + resolve(url));
            downEntity.downPath(file.getPath());
            downEntity.downName(destFileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;
                downEntity.currentByte(finalSum);
                downEntity.totalByte(total);
                downEntity.downStatue(sum == -1);
                callback.onProgress(downEntity);
            }
            fos.flush();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String resolve(String downHttpUrl) {
        String httpUrl = "mp4";
        String[] downArray = downHttpUrl.split(".");
        int length = downArray.length;
        if (length > 0) {
            httpUrl = downArray[length - 1];
        } else if (downHttpUrl.contains(".m3u8")) {
            httpUrl = "m3u8";
        } else if (downHttpUrl.contains("m3u8?")) {
            httpUrl = "m3u8";
        }
        return httpUrl;
    }
}
