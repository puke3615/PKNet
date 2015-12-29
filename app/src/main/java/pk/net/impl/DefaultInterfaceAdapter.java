package pk.net.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import pk.net.anno.Config;
import pk.net.anno.Param;
import pk.net.anno.URL;
import pk.net.core.IRequest;
import pk.net.core.Type;
import pk.net.plug.InterfaceAdapter;

/**
 * @author wzj
 * @version 2015/11/18
 * @Mark 默认的接口转型IRequest适配器
 */
public class DefaultInterfaceAdapter implements InterfaceAdapter, IRequest {

    private StringBuilder mUrl;
    private boolean mSupportCache;
    private Type mType;
    private Map<String, Object> mMap;

    private static String baseUrl;
    private static boolean defaultSupportCache = false;//默认不支持缓存
    private static Type defaultType = Type.POST;//默认是POST请求

    @Override
    public IRequest doAdapter(Class<?> cls, Method method, Object[] args) throws Exception {
        initParams();
        createUrl(cls, method);
        createConfig(cls, method);
        createParams(method, args);
        return this;
    }

    private void initParams() {
        this.mUrl = new StringBuilder(baseUrl == null ? "" : baseUrl);
        removeLastSeparator();
        this.mType = defaultType;
        this.mSupportCache = defaultSupportCache;
        this.mMap = new HashMap<>();
    }


    //==================================构建URL部分======================================

    //根据注解构建Url
    private void createUrl(Class<?> cls, Method method) {
        if (cls.isAnnotationPresent(URL.class)) {
            addUrl(cls.getAnnotation(URL.class).value());
        }
        if (method.isAnnotationPresent(URL.class)) {
            addUrl(method.getAnnotation(URL.class).value());
        }
    }

    //移除url后的最后一个分隔符"/"，如果有
    private void removeLastSeparator() {
        if (mUrl.length() > 0 && mUrl.charAt(mUrl.length() - 1) == '/') {
            mUrl.deleteCharAt(mUrl.length() - 1);
        }
    }

    //后面追加url，并格式化
    private void addUrl(String url) {
        if (mUrl.length() > 0 && url.length() > 0 && !url.startsWith("/")) {
            mUrl.append("/");
        }
        mUrl.append(url);
        removeLastSeparator();
    }

    //==================================构建配置部分======================================
    private void createConfig(Class<?> cls, Method method) {
        if (cls.isAnnotationPresent(Config.class)) {
            Config clsConfig = cls.getAnnotation(Config.class);
            mType = clsConfig.type();
            mSupportCache = clsConfig.supportCache();
        }
        if (method.isAnnotationPresent(Config.class)) {
            Config methodUrl = method.getAnnotation(Config.class);
            mType = methodUrl.type();
            mSupportCache = methodUrl.supportCache();
        }

    }

    //==================================构建参数部分======================================
    private void createParams(Method method, Object[] args) {
        Annotation[][] annoss = method.getParameterAnnotations();
        for (int i = 0; i < annoss.length - 1; i++) {
            Annotation[] annos = annoss[i];
            for (Annotation anno : annos) {
                if (anno instanceof Param) {
                    Param params = (Param) anno;
                    mMap.put(params.value(), args[i]);
                    break;
                }
            }
        }
    }

    @Override
    public String getUrl() {
        return mUrl.toString();
    }

    @Override
    public Type getType() {
        return mType;
    }

    @Override
    public Map<String, Object> getParams() {
        return mMap;
    }

    @Override
    public boolean supportCache() {
        return mSupportCache;
    }

    /**
     * 设置根Url
     * @param baseUrl
     */
    public static void setBaseUrl(String baseUrl) {
        DefaultInterfaceAdapter.baseUrl = baseUrl;
    }

    /**
     * 设置是否支持缓存
     * @param defaultSupportCache
     */
    public static void setDefaultSupportCache(boolean defaultSupportCache) {
        DefaultInterfaceAdapter.defaultSupportCache = defaultSupportCache;
    }

    /**
     * 设置默认请求方式
     * @param defaultType
     */
    public static void setDefaultType(Type defaultType) {
        DefaultInterfaceAdapter.defaultType = defaultType;
    }
}
