package pk.net.plug;

import pk.net.core.IRequest;
import pk.net.core.IResult;
import pk.net.core.ITask;

/**
 * @author wzj
 * @version 2015-8-25
 * @Mark 执行器插件
 * 最基础的网络执行处理
 * 接收IRequest参数，执行的结果通过IResult进行回调
 */
public interface IExecuteHandler {

    /**
     * 方法在主线程调用，然后通过异步任务执行结束后，最终仍在主线程回调
     **/
    ITask execute(IRequest request, Class resultType, IResult result);

}
