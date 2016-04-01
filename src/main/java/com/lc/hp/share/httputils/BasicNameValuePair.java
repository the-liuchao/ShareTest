package com.lc.hp.share.httputils;

import com.lc.hp.share.httputils.impl.NameValuePair;

import java.io.Serializable;

/**
 * Created by hp on 2016/3/31.
 */
public class BasicNameValuePair implements Cloneable, Serializable, NameValuePair {

    private final String name;
    private final String value;
    public static final int HASH_SEED = 17;
    public static final int HASH_OFFSET = 37;

    public BasicNameValuePair(String name, String value) {
        this.name = notNull(name, "Name");
        this.value = value;
    }

    public String getName() {
        return null;
    }

    public String getValue() {
        return null;
    }

    public boolean equals(final Object object) {
        if (this == object)
            return true;
        if (object instanceof NameValuePair) {
            final BasicNameValuePair that = (BasicNameValuePair) object;
            return this.name.equals(that.name) && equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = HASH_SEED;
        hash = hashCode(hash, this.name);
        hash = hashCode(hash, this.value);
        return hash;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public static <T> T notNull(final T argument, final String name) {
        if (argument == null)
            throw new IllegalArgumentException(name + " may not be null");
        return argument;
    }

    public static boolean equals(final Object obj1, final Object obj2) {
        return obj1 == null ? obj2 == null : obj1.equals(obj2);
    }

    public static int hashCode(final int seed, final int hashCode) {
        return seed * HASH_OFFSET + hashCode;
    }

    public static int hashCode(final int seed, final boolean b) {
        return hashCode(seed, b ? 1 : 0);
    }

    public static int hashCode(final int seed, final Object obj) {
        return hashCode(seed, obj != null ? obj.hashCode() : 0);
    }
}
