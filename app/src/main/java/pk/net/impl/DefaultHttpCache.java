package pk.net.impl;

import android.util.LruCache;

import pk.net.core.ITask;

/**
 * @author wzj
 * @version 2015/11/20
 * @Mark
 */
public class DefaultHttpCache implements ITask.ICache {

    private static final int maxSize = 4 * 1024 * 1024; // 4MiB;

    private LruCache<String, String> mCaches = new LruCache<String, String>(maxSize);

    private DefaultHttpCache() {
    }

    private static class DefaultHttpCacheHolder {
        static DefaultHttpCache defaultHttpCache = new DefaultHttpCache();
    }

    public static DefaultHttpCache getInstance() {
        return DefaultHttpCacheHolder.defaultHttpCache;
    }

    @Override
    public void setCache(String url, String result) {
        mCaches.put(url, result);
    }

    @Override
    public String getCache(String url) {
        return mCaches.get(url);
    }
}