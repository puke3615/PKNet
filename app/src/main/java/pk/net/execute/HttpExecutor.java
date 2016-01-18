package pk.net.execute;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pk.net.PKException;
import pk.net.core.IRequest;
import pk.net.core.IResult;
import pk.net.core.ITask;
import pk.net.plug.IExecuteHandler;

/**
 * @author zijiao
 * @version 2016/1/18
 * @Mark 执行器引擎，赋值提交网络请求
 */
public class HttpExecutor extends ThreadPoolExecutor implements IExecuteHandler {

    private static final int DEFAULT_CORE_SIZE = 5;
    private static final int DEFAULT_KEEP_TIME = 60;
    private final Set<ITask> mTasks = new HashSet<>();
    private final List<IFinishListener> mFinishListeners = new ArrayList<>();

    private ITask.ICache mCache;
    private ITask.ITaskCreator mTaskCreator;

    private static class HttpExecutorHolder {
        static HttpExecutor instance = new HttpExecutor();
    }

    private HttpExecutor() {
        super(DEFAULT_CORE_SIZE,
                DEFAULT_CORE_SIZE,
                DEFAULT_KEEP_TIME, TimeUnit.SECONDS,
                new PriorityBlockingQueue<Runnable>(),
                new DiscardPolicy());
    }

    public HttpExecutor configureCache(ITask.ICache cache) {
        this.mCache = cache;
        return this;
    }

    public HttpExecutor configureTaskCreator(ITask.ITaskCreator creator) {
        this.mTaskCreator = creator;
        return this;
    }

    public static HttpExecutor instance() {
        return HttpExecutorHolder.instance;
    }

    @Override
    public ITask execute(IRequest request, Class resultType, IResult result) {
        if (request == null) {
            throw new PKException("the request is null");
        }


        ITask task = new RequestTask(request, this, mCache, result, resultType);
        if (mTaskCreator != null) {
            mTaskCreator.afterCreator(task);
        }
        if (task == null) {
            throw new PKException("the task create failed");
        } else if (task.isCancel()) {
            return task;
        }
        synchronized (mTasks) {
            if (mTasks.contains(task)) {
                throw new PKException("the request is executed");
            }
            execute(task);
            mTasks.add(task);
        }
        return task;
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        if (!(r instanceof ITask)) {
            throw new PKException("the runnable of HttpExecutor isn`t instance of ITask");
        }
    }

    void finish(ITask task) {
        synchronized (mTasks) {
            mTasks.remove(task);
        }
        notifyFinish(task);
    }

    public void cancel(ITask task) {
        if (task != null) {
            task.cancel();
        }
    }

    public void cancelAll(final Object tag) {
        cancelAll(new ITaskChooser() {
            @Override
            public boolean doChoose(ITask task) {
                return tag == null ? task.tag() == null : tag.equals(task.tag());
            }
        });
    }

    public void cancelAll(ITaskChooser chooser) {
        if (chooser != null) {
            synchronized (mTasks) {
                for (ITask task : mTasks) {
                    if (task != null && task.supportCancel() && chooser.doChoose(task)) {
                        task.cancel();
                    }
                }
            }
        }
    }

    public void cancelAll() {
        cancelAll(new ITaskChooser() {
            @Override
            public boolean doChoose(ITask task) {
                return true;
            }
        });
    }

    private void notifyFinish(ITask task) {
        if (task != null) {
            synchronized (mFinishListeners) {
                for (IFinishListener finishListener : mFinishListeners) {
                    if (finishListener != null) {
                        finishListener.onFinish(task);
                    }
                }
            }
        }
    }

    public void register(IFinishListener listener) {
        if (listener != null) {
            synchronized (mFinishListeners) {
                if (!mFinishListeners.contains(listener)) {
                    mFinishListeners.add(listener);
                }
            }
        }
    }

    public void unRegister(IFinishListener listener) {
        if (listener != null) {
            synchronized (mFinishListeners) {
                if (mFinishListeners.contains(listener)) {
                    mFinishListeners.remove(listener);
                }
            }
        }
    }

    public void unRegisterAll() {
        synchronized (mFinishListeners) {
            mFinishListeners.clear();
        }
    }

    public static interface ITaskChooser {
        boolean doChoose(ITask task);
    }

}
