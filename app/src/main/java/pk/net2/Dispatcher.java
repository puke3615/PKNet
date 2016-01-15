package pk.net2;

import android.database.Observable;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zijiao
 * @version 2016/1/15
 * @Mark
 */
public class Dispatcher extends Observable<IFinishListener> implements IDispatcherForRequest {

    private final Set<Request> mRequests = new HashSet<>();
    private HttpConfig mConfig;

    private Dispatcher() {}

    private static class DispatcherHolder {
        static Dispatcher instance = new Dispatcher();
    }

    public static Dispatcher instance() {
        return DispatcherHolder.instance;
    }

    private final ThreadPoolExecutor mPool = new ThreadPoolExecutor(5, 5, 60, TimeUnit.SECONDS,
            new PriorityBlockingQueue<Runnable>(100),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    return thread;
                }
            },
            new ThreadPoolExecutor.DiscardPolicy());

    public void setConfig(HttpConfig config) {
        this.mConfig = config;
    }

    @Override
    public HttpConfig getConfig() {
        return mConfig;
    }

    public <T> Request execute(Request task, IResult<T> result) {
        if (task == null) {
            Exceptions.n("the task is null");
        }

        if (!mRequests.contains(task) && !task.isCancel() && !task.isFinish()) {
            if (!task.isSupport()) {
                Exceptions.noSupport("the request is not support");
            }
            task.setResultListener(result);
            mPool.execute(task);
            mRequests.add(task);
        }
        return task;
    }

    public void cancel(Request task) {
        task.cancel();
    }

    public void cancelByTag(final Object tag) {
        cancelByChoose(new IRequestChooser() {
            @Override
            public boolean doChoose(Request task) {
                return tag == task.getTag();
            }
        });
    }

    public void cancelByChoose(IRequestChooser filter) {
        for (Request request : mRequests) {
            if (filter == null || filter.doChoose(request)) {
                request.cancel();
            }
        }
    }

    public void cancelAll() {
        cancelByChoose(null);
    }

    @Override
    public void finish(Request task) {
        synchronized (mObservers) {
            mRequests.remove(task);
            for (IFinishListener finishListener : mObservers) {
                finishListener.onFinish(task);
            }
        }
    }

    public static interface IRequestChooser {

        boolean doChoose(Request task);

    }

}
