package pk.net.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import pk.net.anno.Config;
import pk.net.anno.Param;
import pk.net.anno.URL;
import pk.net.core.IRequest;
import pk.net.core.ITask;
import pk.net.core.Type;
import pk.net.plug.InterfaceAdapter;

/**
 * @author wzj
 * @version 2015/11/18
 * @Mark 默认的接口转型IRequest适配器
 */
public class DefaultInterfaceAdapter implements InterfaceAdapter {

    private static String baseUrl;                                          //跟url
    private static boolean defaultSupportCache = false;                     //默认不支持缓存
    private static Type defaultType = Type.POST;                            //默认是POST请求
    private static IRequest.Priority defaultPriority = IRequest.Priority.Normal; //默认优先级

    @Override
    public IRequest doAdapter(Class<?> cls, Method method, Object[] args) throws Exception {
        return new Request(cls, method, args);
    }

    /**
     * 设置根Url
     * @param baseUrl
     */
    public static void setBaseUrl(String baseUrl) {
        DefaultInterfaceAdapter.baseUrl = baseUrl;
    }

    /**
     * 设置是否支持缓存（注：该设置只在未配置Config注解时生效）
     * @param defaultSupportCache
     */
    public static void setDefaultSupportCache(boolean defaultSupportCache) {
        DefaultInterfaceAdapter.defaultSupportCache = defaultSupportCache;
    }

    /**
     * 设置默认请求方式（注：该设置只在未配置Config注解时生效）
     * @param defaultType
     */
    public static void setDefaultType(Type defaultType) {
        DefaultInterfaceAdapter.defaultType = defaultType;
    }

    /**
     * 设置默认的请求优先级（注：该设置只在未配置Config注解时生效）
     * @param priority
     */
    public static void setDefaultPriority(IRequest.Priority priority) {
        DefaultInterfaceAdapter.defaultPriority = priority;
    }

    public static class Request implements IRequest {

        private StringBuilder mUrl;
        private boolean mSupportCache;
        private Type mType;
        private Map<String, Object> mMap;
        private Priority mPriority;

        public Request(Class cls, Method method, Object[] args) {
            initParams();
            createUrl(cls, method);
            createConfig(cls, method);
            createParams(method, args);
        }

        private void initParams() {
            this.mUrl = new StringBuilder(baseUrl == null ? "" : baseUrl);
            removeLastSeparator();
            this.mType = defaultType;
            this.mSupportCache = defaultSupportCache;
            this.mMap = new ConcurrentHashMap<>();
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
            if (mUrl != null && mUrl.length() > 0 && mUrl.charAt(mUrl.length() - 1) == '/') {
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
                mPriority = clsConfig.priority();
            }
            if (method.isAnnotationPresent(Config.class)) {
                Config methodConfig = method.getAnnotation(Config.class);
                mType = methodConfig.type();
                mSupportCache = methodConfig.supportCache();
                mPriority = methodConfig.priority();
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

        @Override
        public Priority getPriority() {
            return null;
        }

    }
}
