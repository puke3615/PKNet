package pk.net.core;

/**
 * @author zijiao
 * @version 2016/1/18
 * @Mark 网络请求任务接口
 */
public interface ITask extends Runnable, Comparable<ITask> {

    /**
     * 返回请求的原始信息
     * @return
     */
    IRequest getRequest();

    /**
     * 该请求是否支持取消
     * @return
     */
    boolean supportCancel();

    /**
     * 取消该请求
     */
    void cancel();

    /**
     * 返回请求tag
     * @return
     */
    Object tag();

    /**
     * 设置请求tag
     * @param tag
     */
    void setTag(Object tag);

    /**
     * 返回请求优先级
     * @return
     */
    IRequest.Priority getPriority();

    /**
     * 设置请求优先级
     * @param priority
     */
    void setPriority(IRequest.Priority priority);

    /**
     * 请求是否取消
     * @return
     */
    boolean isCancel();

    /**
     * 缓存
     */
    public static interface ICache {

        void setCache(String key, String result);

        String getCache(String key);

    }

    /**
     * 任务创造器
     */
    public static interface ITaskCreator {
        /**
         * 任务被创建后回调，执行于主线程
         * @param task
         */
        void afterCreator(ITask task);
    }

}
