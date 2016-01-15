package pk.net.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import pk.net.PKHttpException;
import pk.net.plug.IExecuteHandler;
import pk.net.plug.IResultTypeAccessor;
import pk.net.plug.InterfaceAdapter;


/**
 * @author wzj
 * @version 2015-8-24
 * @Mark 接口代理类
 * 集成了网络执行，接口转型，数据适配回调等核心功能
 * 该类专注于代理类的生成过程，将网络部分的处理转交IExecuteHandler进行处理
 * 为了提高拓展性，将关键功能以接口的形式进行调用，方便外部程序以插件的方式进行功能拓展
 */
public final class HttpProxyFactory {

    //the adapter of conver from class to IRequest
    private InterfaceAdapter mInterfaceAdapter;

    //the executor of execute net request
    private IExecuteHandler mExecuteHandler;

    private IResultTypeAccessor mResultAccessor;


    public final <T> T createProxy(Class<T> interfaceClass) {
        if (interfaceClass == null || !interfaceClass.isInterface()) {
            throw new RuntimeException("传入的参数为null或者不是一个接口");
        }
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new DefaultInvocationHandler(interfaceClass));
    }


    /**
     * 设置接口转型适配器，用于把接口转型为IRequest
     **/
    public final HttpProxyFactory setInterfaceAdapter(InterfaceAdapter mInterfaceAdapter) {
        this.mInterfaceAdapter = mInterfaceAdapter;
        return this;
    }

    /**
     * 设置执行器
     **/
    public final HttpProxyFactory setExecuteHandler(IExecuteHandler mExecuteHandler) {
        this.mExecuteHandler = mExecuteHandler;
        return this;
    }

    /**
     * 设置转型接口
     *
     * @param mResultAccessor
     * @return
     */
    public final HttpProxyFactory setResultAccessor(IResultTypeAccessor mResultAccessor) {
        this.mResultAccessor = mResultAccessor;
        return this;
    }

    private class DefaultInvocationHandler implements InvocationHandler {

        Class<?> mInterfaceClass;

        DefaultInvocationHandler(Class<?> interfaceClass) {
            mInterfaceClass = interfaceClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            IRequest request = null;
            try {
                request = mInterfaceAdapter.doAdapter(mInterfaceClass, method, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (request == null) {
                throw new PKHttpException("接口类转型IRequest失败");
            }
            if (args == null || args.length == 0) {
                throw new PKHttpException(mInterfaceClass.getName() + "的" + method.getName() + "方法中，参数设置错误");
            }

            Object result = args[args.length - 1];
            if (result instanceof IResult) {
                if (mExecuteHandler == null) {
                    throw new PKHttpException("未设置网络请求执行类IExecuteHandler");
                }

                if (mResultAccessor == null) {
                    throw new PKHttpException("未设置泛型类型获取器IResultTypeAccessor");
                }

                IResult resultListener = (IResult) result;
                Class resultType = null;
                try {
                    resultType = mResultAccessor.accessType(proxy.getClass(), method, resultListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (resultType == null) {
                    throw new PKHttpException("泛型的真实类型获取失败");
                }

                try {
                    mExecuteHandler.execute(request, resultType, resultListener);
                } catch (Exception e) {
                    e.printStackTrace();
                    resultListener.onResult(Result.createError("网络请求执行异常"));
                }

            } else {
                throw new PKHttpException(mInterfaceClass.getName() + "的" + method.getName() + "方法中，IResult的参数位置不正确");
            }

            return request;
        }
    }

}
