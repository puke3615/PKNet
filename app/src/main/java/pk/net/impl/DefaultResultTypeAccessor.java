package pk.net.impl;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import pk.net.core.IResult;
import pk.net.plug.IResultTypeAccessor;

/**
 * @author wzj
 * @version 2015/11/18
 * @Mark 默认的泛型类型获取器
 */
public class DefaultResultTypeAccessor implements IResultTypeAccessor {

    public static ParameterizedType getParameterizedType(Class<?> cls) {
        ParameterizedType pt = null;
        Class<?> superCls = cls.getSuperclass();
        if (superCls == Object.class) {
            pt = (ParameterizedType) cls.getGenericInterfaces()[0];
        } else {
            pt = (ParameterizedType) cls.getGenericSuperclass();
        }
        return pt;
    }

    @Override
    public Class accessType(Class proxyCls, Method method, IResult result) {
        Class resultCls = result.getClass();
        ParameterizedType pt = getParameterizedType(resultCls);
        return (Class) pt.getActualTypeArguments()[0];
    }

}
