package com.lc.hp.share.httputils;

import com.lc.hp.share.httputils.common.BaseParams;
import com.lc.hp.share.httputils.impl.NameValuePair;

import java.net.FileNameMap;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.BitSet;
import java.util.List;

/**
 * Created by liuchao on 2016/3/31.
 */
public class UrlEncodeUtils {
    private static final char QP_SEP_A = '&';
    private static final char QP_SEP_S = ';';
    private static final String NAME_VALUE_SEPARATOR = "=";
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final Charset ASCII = Charset.forName("US-ASCII");
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");


    public static String format(final List<BasicNameValuePair> parameters,
                                final char parameterSeprator,
                                final Charset charset) {
        final StringBuilder result = new StringBuilder();
        for (final NameValuePair parameter : parameters) {
            final String encodeName = encodeFormFields(parameter.getName(), charset);
            final String encodeValue = encodeFormFields(parameter.getValue(), charset);
            if (result.length() > 0) {
                result.append(parameterSeprator);
                result.append(encodeName);
                if (encodeValue != null) {
                    result.append(NAME_VALUE_SEPARATOR);
                    result.append(encodeValue);
                }
            }
        }
        return result.toString();
    }

    public static String format(final Iterable<? extends NameValuePair> parameters,
                                final Charset charset) {
        return format(parameters, QP_SEP_A, charset);
    }

    public static String format(final Iterable<? extends NameValuePair> parameters,
                                final char parameterSeparator,
                                final Charset charset){
      final StringBuilder result = new StringBuilder();
        for (final NameValuePair parameter : parameters
             ) {
            final String encodeName = encodeFormFields(parameter.getName(),charset);
            final String encodeValue = encodeFormFields(parameter.getValue(),charset);
            if(result.length()>0)
                result.append(parameterSeparator);
            if(encodeValue!=null){
                result.append(NAME_VALUE_SEPARATOR);
                result.append(encodeValue);
            }
        }
        return result.toString();
    }

    private static final BitSet UNRESERVED = new BitSet(256);
    private static final BitSet PUNCT = new BitSet(256);
    private static final BitSet URIC = new BitSet(256);
    private static final BitSet PATHSAFE = new BitSet(256);
    private static final BitSet USERINFO = new BitSet(256);
    private static final BitSet URLENCODER   = new BitSet(256);

    private static String encodeFormFields(final String content,final String charset){
          if(content == null)
              return null;
        return urlEncode(content,charset!=null?Charset.forName(charset):UTF_8,URLENCODER,true);
    }

    private static String encodeFormFields(final String content,final Charset charset){
        if(content==null)
            return null;
        return urlEncode(content, charset != null ? charset : Charset.forName("UTF-8"), URLENCODER, true);
    }
    private static final int RADIX = 16;

    private static String urlEncode(final String content,
                                    final Charset charset,
                                    final BitSet safchars,
                                    final boolean blankAsPlus) {
        if (content == null)
            return null;
        final StringBuilder buf = new StringBuilder();
        final ByteBuffer bb = charset.encode(content);
        while (bb.hasRemaining()) {
            final int b = bb.get() & 0xff;
            if (safchars.get(b)) {
                buf.append((char) b);
            } else if (blankAsPlus && b == ' ') {
                buf.append('+');
            } else {
                buf.append("%");
                final char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xf, RADIX));
                final char hex2 = Character.toUpperCase(Character.forDigit(b & 0xf, RADIX));
                buf.append(hex1);
                buf.append(hex2);
            }
        }
        return buf.toString();
    }

    private static String urlDecode(final String content,
                                    final Charset charset,
                                    final boolean plusAsBlank) {
        if (content == null)
            return null;
        final ByteBuffer bb = ByteBuffer.allocate(content.length());
        final CharBuffer cb = CharBuffer.wrap(content);
        while (cb.hasRemaining()) {
            final char c = cb.get();
            if (c == '%' && cb.remaining() >= 2) {
                final char uc = cb.get();
                final char lc = cb.get();
                final int u = Character.digit(uc, 16);
                final int l = Character.digit(lc, 16);
                if (u != -1 && l != -1) {
                    bb.put((byte) ((u << 4) + 1));
                } else {
                    bb.put((byte) '%');
                    bb.put((byte) uc);
                    bb.put((byte) lc);
                }
            } else if (plusAsBlank && c == '+') {
                bb.put((byte) ' ');
            } else {
                bb.put((byte) c);
            }
        }
        bb.flip();
        return charset.decode(bb).toString();
    }

    static String encUserInfo(final String content,final Charset charset){
        return urlEncode(content, charset, USERINFO, false);
    }

    static String encUric(final String content,final Charset charset){
        return urlEncode(content, charset, URIC, false);
    }
    static String encPath(final String content,final Charset charset){
        return urlEncode(content,charset,PATHSAFE,false);
    }

    public static String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    public static String format(
            final List<BasicNameValuePair> parameters,
            final String charset) {
        return format(parameters, QP_SEP_A, charset);
    }

    /**
     * Returns a String that is suitable for use as an {@code application/x-www-form-urlencoded}
     * list of parameters in an HTTP PUT or HTTP POST.
     *
     * @param parameters  The parameters to include.
     * @param parameterSeparator The parameter separator, by convention, {@code '&'} or {@code ';'}.
     * @param charset The encoding to use.
     * @return An {@code application/x-www-form-urlencoded} string
     *
     * @since 4.3
     */
    public static String format(
            final List<BasicNameValuePair> parameters,
            final char parameterSeparator,
            final String charset) {
        final StringBuilder result = new StringBuilder();
        for (final NameValuePair parameter : parameters) {
            final String encodedName = encodeFormFields(parameter.getName(), charset);
            final String encodedValue = encodeFormFields(parameter.getValue(), charset);
            if (result.length() > 0) {
                result.append(parameterSeparator);
            }
            result.append(encodedName);
            if (encodedValue != null) {
                result.append(NAME_VALUE_SEPARATOR);
                result.append(encodedValue);
            }
        }
        return result.toString();
    }

    public static String getUrlWithQueryString(boolean shouldEncodeUrl, String url, BaseParams params) {
        if (url == null)
            return null;
        if (shouldEncodeUrl) {
            try {
                String decodedURL = URLDecoder.decode(url, "UTF-8");
                URL _url = new URL(decodedURL);
                URI _uri = new URI(_url.getProtocol(), _url.getUserInfo(), _url.getHost(), _url.getPort(), _url.getPath(), _url.getQuery(), _url.getRef());
                url = _uri.toASCIIString();
            } catch (Exception ex) {
            }
        }

        if (params != null) {
            String paramString = params.getParamString().trim();
            if (!paramString.equals("") && !paramString.equals("?")) {
                url += url.contains("?") ? "&" : "?";
                url += paramString;
            }
        }

        return url;
    }
}
