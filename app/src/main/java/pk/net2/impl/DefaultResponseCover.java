package pk.net2.impl;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;

import pk.net2.IResponseCover;

/**
 * @author zijiao
 * @version 2016/1/15
 * @Mark
 */
public class DefaultResponseCover implements IResponseCover {

    private Gson gson = new Gson();

    @Override
    public <T> T cover(String result) {
        Class resultCls = result.getClass();
        ParameterizedType pt = getParameterizedType(resultCls);
        Class<T> cls = (Class) pt.getActualTypeArguments()[0];
        return gson.fromJson(result, cls);
    }

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

}
