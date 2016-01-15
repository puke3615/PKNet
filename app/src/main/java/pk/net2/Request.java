package pk.net2;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import pk.net2.impl.DefaultExecute;
import pk.net2.impl.DefaultResponseCover;

/**
 * @author zijiao
 * @version 2016/1/15
 * @Mark
 */
public class Request<T> extends BaseRequest<Request<T>> implements Runnable, Comparable<Request> {

    private ICache mCache;
    private IExecute mExecute;
    private IResult<T> mResultListener;
    private IResponseCover mResponseCover;
    private IDispatcherForRequest mDispatcher;

    private Object mTag = null;
    private boolean mIsFinish = false;
    private boolean mIsCancel = false;
    private boolean mCanCache = true;
    private Priority mPriority = Priority.Normal;

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void run() {
        if (mIsCancel) {
            finish();
            return;
        }

        Response response = null;
        if (mCanCache && mCache != null) {
            response = mCache.getCache();
        }
        if (response == null || response.get() == null) {
            response = mExecute.getResponse(this);
        }

        if (mIsCancel) {
            finish();
            return;
        }

        if (response != null && response.isSuccess()) {
            String result = response.getResult();
            if (!TextUtils.isEmpty(result)) {
                T t = mResponseCover.cover(result);
                if (t == null) {
                    response.setError(Response.Error.COVER_ERROR);
                } else {
                    response.set(t);
                }
            }
        }

        final Response r = response;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mResultListener != null) {
                    mResultListener.onResult(r);
                }
                finish();
            }
        });
    }

    public void setResultListener(IResult<T> resultListener) {
        this.mResultListener = resultListener;
    }

    public void finish() {
        mIsFinish = true;
        mDispatcher.finish(this);
    }

    public Priority getPriority() {
        return mPriority;
    }

    public boolean canCache() {
        return mCanCache;
    }

    public void cancel() {
        this.mIsCancel = true;
    }

    public boolean isCancel() {
        return mIsCancel;
    }

    public boolean isFinish() {
        return mIsFinish;
    }

    public Object getTag() {
        return mTag;
    }

    public Request setTag(Object tag) {
        this.mTag = tag;
        return this;
    }

    public Request setPriority(Priority priority) {
        this.mPriority = priority;
        return this;
    }

    public Request setCanCache(boolean canCache) {
        this.mCanCache = canCache;
        return this;
    }

    public Request<T> create() {
        mDispatcher = Dispatcher.instance();
        HttpConfig config = mDispatcher.getConfig();
        if (config != null) {
            this.mCache = config.mCache;
            this.mExecute = config.mExecute;
            this.mResponseCover = config.mResponseCover;
        }
        if (mExecute == null) {
            mExecute = new DefaultExecute();
        }
        if (mResponseCover == null) {
            mResponseCover = new DefaultResponseCover();
        }
        return this;
    }

    @Override
    public int compareTo(Request another) {
        return another == null ? 0 : -mPriority.compareTo(another.mPriority);
    }

    public boolean isSupport() {
        switch (getMethod()) {
            case GET:
            case POST:
                return true;
            default:
                return false;
        }
    }

    public static enum Priority {
        LOWER, LOW, Normal, High, Higher;
    }

}
