package pk.net;

import java.util.HashMap;
import java.util.Map;

import pk.net.core.HttpProxyFactory;
import pk.net.core.IRequest;
import pk.net.core.ITask;
import pk.net.core.Type;
import pk.net.execute.HttpExecutor;
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
        /**针对impl包的配置**/
//        DefaultInterfaceAdapter.setDefaultType(Type.GET);                   //设置默认的请求方式
//        DefaultInterfaceAdapter.setDefaultSupportCache(true);               //设置默认是否支持缓存
//        DefaultExecuteHandler.setHttpCache(DefaultHttpCache.getInstance()); //设置缓存
//        mHttpProxyFactory = new HttpProxyFactory()                          //生成接口的代理类
//                .setResultAccessor(new DefaultResultTypeAccessor())         //设置结果类型获取接口
//                .setInterfaceAdapter(new DefaultInterfaceAdapter())         //设置接口转型适配器
//                .setExecuteHandler(new DefaultExecuteHandler());            //设置网络执行器

        //========================================分割线=============================================

        /**改进版配置（针对execute包的配置），新增：
         * 1. 任务可取消，可设置请求生命周期联动
         * 2. 将IExecuteHandler的实现类由DefaultExecuteHandler换成HttpExecutor，增强拓展性
         * 3. 增加请求任务的结束回调
         * 4. 增加了请求优先级
         **/
        HttpExecutor executor = HttpExecutor.instance()
                .configureCache(DefaultHttpCache.getInstance())
                .configureTaskCreator(new ITask.ITaskCreator() {
                    @Override
                    public void afterCreator(ITask task) {
                        task.setPriority(IRequest.Priority.Normal);
                    }
                });
        mHttpProxyFactory = new HttpProxyFactory()
                .setInterfaceAdapter(new DefaultInterfaceAdapter())
                .setResultAccessor(new DefaultResultTypeAccessor())
                .setExecuteHandler(executor);
    }


    /**
     * 获取网络请求的代理类
     *
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
