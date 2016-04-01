package com.lc.hp.share.httputils.common;

import com.lc.hp.share.httputils.BasicNameValuePair;
import com.lc.hp.share.httputils.UrlEncodeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hp on 2016/3/31.
 */
public class BaseParams {


    public final static String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public final static String APPLICATION_JSON = "application/json";

    public final ConcurrentHashMap<String, String> strParams = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, StreamWrapper> streamParams = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, FileWrapper> fileParams = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, List<FileWrapper>> fileArrayParams = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, Object> objParams = new ConcurrentHashMap<>();
    protected boolean isRepeatable;
    protected boolean forceMultipartEntity = false;
    protected boolean useJsonStreamer;
    protected String elapsedFieldInJsonStreamer = "_elapsed";
    protected boolean autoCloseInputStreams;
    protected String contentEncoding = "UTF-8";

    public String url;
    public int page;//页码
    public String paserType;//类型

    /**
     * 设置网络关闭tag
     */
    public Object tag=null;
    /**
     * 设置网络关闭tag
     * @param object
     */
    public void setTag(Object object) {
        tag=object;
    }


    public BaseParams(Map<String, String> source) {
        if (source != null) {
            for (Map.Entry<String, String> entry : source.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    public BaseParams(final String key, final String value) {
        this(new HashMap<String, String>() {{
            put(key, value);
        }});
    }

    public BaseParams(Object... keysAndValues) {
        int len = keysAndValues.length;
        if (len % 2 != 0)
            throw new IllegalArgumentException("Supplied arguments must be even");
        for (int i = 0; i < len; i += 2) {
            String key = String.valueOf(keysAndValues[i]);
            String val = String.valueOf(keysAndValues[i + 1]);
            put(key, val);
        }
    }

    public void setForceMultipartEntityContentType(boolean force) {
        this.forceMultipartEntity = force;
    }

    public void put(String key, String value) {
        if (key != null && value != null) {
            strParams.put(key, value);
        }
    }

    public void put(String key, File files[]) throws FileNotFoundException {
        put(key, files, null, null);
    }

    private void put(String key, File[] files, String contentType, String customFileName) throws FileNotFoundException {
        if (key != null) {
            List<FileWrapper> fileWrappers = new ArrayList<>();
            for (File file : files
                    ) {
                if (file == null || !file.exists())
                    throw new FileNotFoundException();
                fileWrappers.add(new FileWrapper(file, contentType, customFileName));
            }
            fileArrayParams.put(key, fileWrappers);
        }
    }

    public void put(String key, ArrayList<File> files) throws FileNotFoundException {
        put(key, files, null, null);
    }


    public void put(String key, ArrayList<File> files, String contentType, String customFileName) throws FileNotFoundException {
        if (key != null) {
            List<FileWrapper> fileWrappers = new ArrayList<>();
            for (File file : files) {
                if (file == null || !file.exists())
                    throw new FileNotFoundException();
                fileWrappers.add(new FileWrapper(file, contentType, customFileName));
            }
            fileArrayParams.put(key, fileWrappers);
        }
    }

    public void put(String key, File file) throws FileNotFoundException {
        put(key, file, null, null);
    }

    public void put(String key, String customFileName, File file) throws FileNotFoundException {
        put(key, file, null, customFileName);
    }

    public void put(String key, File file, String contentType) throws FileNotFoundException {
        put(key, file, contentType, null);
    }

    public void put(String key, File file, String contentType, String customFileName) throws FileNotFoundException {
        if (file == null || !file.exists())
            throw new FileNotFoundException();
        if (key != null)
            fileParams.put(key, new FileWrapper(file, contentType, customFileName));
    }

    public void put(String key, InputStream stream, String name) {
        put(key, stream, name, null);
    }

    public void put(String key, InputStream stream, String name, String contentType) {
        put(key, stream, name, contentType, autoCloseInputStreams);
    }

    public void put(String key, InputStream stream, String name, String contentType, boolean autoClose) {
        if (key != null && stream != null) {
            streamParams.put(key, StreamWrapper.newInstance(stream, name, contentType, autoClose));
        }
    }

    public void put(String key, int value) {
        if (key != null) {
            strParams.put(key, String.valueOf(value));
        }
    }

    public void put(String key, long value) {
        if (key != null)
            strParams.put(key, String.valueOf(value));
    }

    public void remove(String key) {
        strParams.remove(key);
        streamParams.remove(key);
        fileParams.remove(key);
        objParams.remove(key);
        fileArrayParams.remove(key);
    }

    public void clear() {
        strParams.clear();
        streamParams.clear();
        fileParams.clear();
        objParams.clear();
        fileArrayParams.clear();
    }

    public boolean has(String key) {
        return strParams.get(key) != null ||
                streamParams.get(key) != null ||
                fileParams.get(key) != null ||
                fileArrayParams.get(key) != null ||
                objParams.get(key) != null;
    }

    public void setHttpEntityIsRepeatable(boolean flag) {
        this.isRepeatable = flag;
    }

    public void setUseJsonStreamer(boolean flag) {
        this.useJsonStreamer = flag;
    }

    protected List<BasicNameValuePair> getParamsList() {
        List<BasicNameValuePair> lparams = new LinkedList<>();
        for (ConcurrentHashMap.Entry<String, String> entry : strParams.entrySet()) {
            lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        lparams.addAll(getParamsList(null, objParams));
        return lparams;
    }

    protected List<BasicNameValuePair> getParamsList(String key, Object value) {
        List<BasicNameValuePair> params = new LinkedList<>();
        if (value instanceof Map) {
            Map map = (Map) value;
            List list = new ArrayList<Object>(map.keySet());
            if (list.size() > 0 && list.get(0) instanceof Comparable) {
                Collections.sort(list);
            }
            for (Object nestedKey : list) {
                if (nestedKey instanceof String) {
                    Object nestedValue = map.get(nestedKey);
                    if (nestedValue != null) {
                        params.addAll(getParamsList(key == null ? (String) nestedKey : String.format(Locale.US, "[%s%s]", key, nestedKey), nestedValue));
                    }
                }
            }
        } else if (value instanceof List) {
            List list = (List) value;
            int listSize = list.size();
            for (int nestedValueIndex = 0; nestedValueIndex < listSize; nestedValueIndex++) {
                params.addAll(getParamsList(String.format(Locale.US, "%s[%d]", key, nestedValueIndex), list.get(nestedValueIndex)));
            }
        } else if (value instanceof Object[]) {
            Object[] array = (Object[]) value;
            int arrayLength = array.length;
            for (int nestedValueIndex = 0; nestedValueIndex < arrayLength; nestedValueIndex++) {
                params.addAll(getParamsList(String.format(Locale.US, "%s[%d]", key, nestedValueIndex), array[nestedValueIndex]));
            }
        } else if (value instanceof Set) {
            Set set = (Set) value;
            for (Object nestedValue : set) {
                params.addAll(getParamsList(key, nestedValue));
            }
        } else {
            params.add(new BasicNameValuePair(key, value.toString()));
        }
        return params;
    }

    public String getParamString() {
        return UrlEncodeUtils.format(getParamsList(), contentEncoding);
    }

    public static class FileWrapper implements Serializable {
        public final File file;
        public final String contentType;
        public final String customFileName;

        public FileWrapper(File file, String contentType, String customFileName) {
            this.file = file;
            this.contentType = contentType;
            this.customFileName = customFileName;
        }
    }

    public static class StreamWrapper {

        public final InputStream inputStream;
        public final String name;
        public final String contentType;
        public final boolean autoClose;

        public StreamWrapper(InputStream inputStream, String name, String contentType, boolean autoClose) {
            this.inputStream = inputStream;
            this.name = name;
            this.contentType = contentType;
            this.autoClose = autoClose;
        }

        static StreamWrapper newInstance(InputStream inputStream, String name, String contentType, boolean autoClose) {
            return new StreamWrapper(inputStream,
                    name,
                    contentType == null ? APPLICATION_OCTET_STREAM : contentType,
                    autoClose);
        }
    }
}
