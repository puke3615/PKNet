package pk.net.plug;

import java.lang.reflect.Method;

import pk.net.core.IRequest;

/**
 * @author wzj
 * @version 2015-8-25
 * @Mark 接口转型适配器插件
 * 负责将一个interface类型及其方法所携带的信息
 * 定向转型为执行器能够识别的IRequest接口
 */
public interface InterfaceAdapter {

    IRequest doAdapter(Class<?> cls, Method method, Object[] args) throws Exception;

}
