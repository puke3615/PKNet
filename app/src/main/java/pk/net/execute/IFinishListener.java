package pk.net.execute;

import pk.net.core.ITask;

/**
 * @author zijiao
 * @version 2016/1/18
 * @Mark 请求结束回调接口
 */
public interface IFinishListener {

    void onFinish(ITask task);

}
