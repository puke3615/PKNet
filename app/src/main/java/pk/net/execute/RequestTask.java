package pk.net.execute;


import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.google.gson.Gson;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import pk.net.core.IRequest;
import pk.net.core.IResult;
import pk.net.core.ITask;
import pk.net.core.Result;
import pk.net.core.Type;

/**
 * @author zijiao
 * @version 2016/1/18
 * @Mark 请求执行任务
 */
public class RequestTask implements ITask {

    private static int waitTimeout = 10000;
    private static int connectTimeout = 5000;

    private boolean isCancel = false;
    private boolean isFinish = false;
    private Object tag;
    private IRequest.Priority priority = IRequest.Priority.Normal;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private static Gson gson = new Gson();
    private IRequest request;
    private ICache cache;
    private HttpExecutor mExecutor;
    private IResult callback;
    private Class resultType;

    RequestTask(IRequest request, HttpExecutor httpExecutor, ICache cache, IResult result, Class resultType) {
        this.request = request;
        this.mExecutor = httpExecutor;
        this.cache = cache;
        this.callback = result;
        this.resultType = resultType;
    }

    @Override
    public void run() {
        if (alreadyCancel()) {
            return;
        }

        String url = request.getUrl();
        Type type = request.getType();
        Map<String, Object> params = request.getParams();
        String urlFlag = createUrl(url, params);
        if (request.supportCache() && cache != null) {//若设置了缓存
            String result = cache.getCache(urlFlag);
            if (result != null) {//且缓存能够取到值
                if (callback != null) {
                    doCallback(result, true, resultType, 200, callback);
                }
                return;
            }
        }

        Response response = null;
        switch (type) {
            case GET:
                response = requestGet(urlFlag);
                break;
            case POST:
                response = requestPost(url, params);
                break;
        }
        if (cache != null
                && request.supportCache()
                && response.responseCode == 200
                && !TextUtils.isEmpty(response.result)) {
            cache.setCache(urlFlag, response.result);
        }

        doCallback(response.result, false, resultType, response.responseCode, callback);

    }

    //拼接请求的url，并将该url作为请求的标识位
    private String createUrl(String url, Map<String, Object> params) {
        if (url == null && params == null || params.size() == 0) {
            return url;
        }
        StringBuilder builder = new StringBuilder(url);
        builder.append("?");
        Iterator<String> iterator = params.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = params.get(key);
            if (key != null && value != null) {
                builder.append(key)
                        .append("=")
                        .append(value)
                        .append("&");
            }
        }
        int length = builder.length();
        if (length > 0 && builder.charAt(length - 1) == '&') {
            builder.deleteCharAt(length - 1);
        }
        return builder.toString();
    }

    //执行Get请求
    private Response requestGet(String urlFlag) {
        String result = null;
        int responseCode = 0;
        try {
            URL url = null;
            url = new URL(urlFlag);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(waitTimeout);
            conn.setRequestMethod("GET");
            result = inputStream2String(conn.getInputStream());
            responseCode = conn.getResponseCode();
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Response(result, responseCode);
    }

    //执行Post请求
    private Response requestPost(String u, Map<String, Object> params) {
        String result = null;
        int responseCode = 0;
        try {
            URL url = new URL(u);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(waitTimeout);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            if (params != null && params.size() != 0) {
                Iterator<String> iterator = params.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    Object value = params.get(key);
                    if (key != null && value != null) {
                        conn.setRequestProperty(key, value.toString());
                    }
                }
            }
            result = inputStream2String(conn.getInputStream());
            responseCode = conn.getResponseCode();
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Response(result, responseCode);
    }

    private void doCallback(final String result, final boolean isCache, final Class resultType, final int responseCode, final IResult callback) {
        if (alreadyCancel()) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (alreadyCancel()) {
                    return;
                } else {
                    finish();
                }
                Object entity = null;
                if (responseCode != 200 && result == null) {
                    callback.onResult(Result.createError("网络状态不好，请稍后再试"));
                    return;
                }
                try {
                    entity = gson.fromJson(result, resultType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (entity == null) {
                    callback.onResult(Result.createError("数据转型失败"));
                } else {
                    callback.onResult(Result.create(entity, "", responseCode, isCache));
                }
            }
        });
    }

    private String inputStream2String(InputStream is) {
        if (is == null || alreadyCancel()) {
            return null;
        }
        String result = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int index = -1;
            while ((index = is.read(buffer)) != -1) {
                baos.write(buffer, 0, index);
                if (alreadyCancel()) {
                    return null;
                }
            }
            result = new String(baos.toByteArray());
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    private boolean alreadyCancel() {
        if (isCancel) {
            if (!isFinish) {
                finish();
            }
            return true;
        }
        return false;
    }

    @Override
    public IRequest getRequest() {
        return request;
    }

    @Override
    public boolean supportCancel() {
        return true;
    }

    private void finish() {
        isFinish = true;
        mExecutor.finish(this);
    }

    @Override
    public void cancel() {
        isCancel = true;
    }

    @Override
    public Object tag() {
        return tag;
    }

    @Override
    public void setTag(Object tag) {
        this.tag = tag;
    }

    @Override
    public IRequest.Priority getPriority() {
        return priority;
    }

    @Override
    public void setPriority(IRequest.Priority priority) {
        if (priority != null) {
            this.priority = priority;
        }
    }

    @Override
    public boolean isCancel() {
        return isCancel;
    }

    @Override
    public int compareTo(ITask another) {
        return another == null ? 0 : another.getPriority().ordinal() - getPriority().ordinal();
    }

    static class Response {
        String result;
        int responseCode;

        public Response(String msg, int responseCode) {
            this.result = msg;
            this.responseCode = responseCode;
        }
    }

}
