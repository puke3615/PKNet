package pk.net.plug;

import java.lang.reflect.Method;

import pk.net.core.IResult;

/**
 * @author wzj
 * @version 2015/11/18
 * @Mark 结果访问器插件
 * 通过给定的参数获取返回结果的真实类型
 */
public interface IResultTypeAccessor {

    Class accessType(Class proxyCls, Method method, IResult result);

}
