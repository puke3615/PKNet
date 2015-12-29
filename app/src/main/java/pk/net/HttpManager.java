package pk.net;

import java.util.HashMap;
import java.util.Map;

import pk.net.core.HttpProxyFactory;
import pk.net.impl.DefaultExecuteHandler;
import pk.net.impl.DefaultHttpCache;
import pk.net.impl.DefaultInterfaceAdapter;
import pk.net.impl.DefaultResultTypeAccessor;


/**
 * @author wzj
 * @version 2015-9-24
 * @Mark
 */
public class HttpManager {

    private static final Map<Class<?>, Object> mProxys = new HashMap<Class<?>, Object>();

    private static HttpProxyFactory mHttpProxyFactory;

    static {
        DefaultExecuteHandler.setHttpCache(DefaultHttpCache.getInstance());
        mHttpProxyFactory = new HttpProxyFactory()
                .setResultAccessor(new DefaultResultTypeAccessor())//设置结果类型获取接口
                .setInterfaceAdapter(new DefaultInterfaceAdapter())//设置接口转型适配器
                .setExecuteHandler(new DefaultExecuteHandler());//设置网络执行器
    }


    /**
     * 获取网络请求的代理类
     * @param cls 网络请求接口
     * @param <T> 接口类型
     * @return 网络执行代理类
     */
    public static <T> T getProxy(Class<T> cls) {
        if (cls == null) {
            return null;
        }

        if (!mProxys.containsKey(cls)) {
            synchronized (HttpManager.class) {
                if (!mProxys.containsKey(cls)) {
                    mProxys.put(cls, mHttpProxyFactory.createProxy(cls));
                }
            }
        }
        return (T) mProxys.get(cls);
    }

}
