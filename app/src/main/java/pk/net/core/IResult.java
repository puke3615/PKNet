package pk.net.core;


/**
 * @author wzj
 * @version 2015-8-24
 * @Mark 回调接口
 */
public interface IResult<T> {

    /**
     * 响应结果时回调该方法
     * 回调该函数只代表网络处理和数据转型正常
     * 具体的业务逻辑还需要具体判断
     *
     * @param result
     */
    void onResult(Result<T> result);


}
