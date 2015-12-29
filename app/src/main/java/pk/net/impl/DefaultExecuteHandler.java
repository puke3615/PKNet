package pk.net.impl;

import android.os.Handler;
import android.os.Looper;

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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pk.net.core.IRequest;
import pk.net.core.IResult;
import pk.net.core.Result;
import pk.net.core.Type;
import pk.net.plug.IExecuteHandler;

/**
 * Created by wzj on 2015/11/17.
 * 对于执行器接口IExecuteHandler默认的简易实现
 */
public class DefaultExecuteHandler implements IExecuteHandler {

    private static int coreSize = 5;//核心线程数
    private static int maxSize = 20;//最大线程数
    private static int aliveTime = 60;//存活时间
    private static int cacheSize = 10;//缓冲任务数

    private static int waitTimeout = 10000;
    private static int connectTimeout = 5000;

    private static final Handler handler = new Handler(Looper.getMainLooper());

    private static IHttpCache cache;

    private static Gson gson = new Gson();

    public interface IHttpCache {
        void setCache(String url, String result);

        String getCache(String url);
    }

    private static ThreadFactory factory = new ThreadFactory() {

        int index = 0;

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "网络子线程" + index++);
            return thread;
        }
    };

    private static ArrayBlockingQueue cacheQueue = new ArrayBlockingQueue<Runnable>(cacheSize);

    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            coreSize,
            maxSize,
            aliveTime, TimeUnit.SECONDS,
            cacheQueue,
            factory,
            new ThreadPoolExecutor.DiscardPolicy());

    @Override
    public void execute(final IRequest request, final Class cls, final IResult callback) {
        if (request == null || callback == null) {
            return;
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                String url = request.getUrl();
                Type type = request.getType();
                Map<String, Object> params = request.getParams();
                String urlFlag = createUrl(url, params);
                if (cache != null) {//若设置了缓存
                    String result = cache.getCache(urlFlag);
                    if (result != null) {//且缓存能够取到值
                        doCallback(result, true, cls, 200, callback);
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
                if (cache != null && request.supportCache() && response.responseCode == 200) {
                    cache.setCache(urlFlag, response.result);
                }

                doCallback(response.result, false, cls, response.responseCode, callback);
            }
        });
    }

    //执行Get请求
    private static Response requestGet(String urlFlag) {
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
    private static Response requestPost(String u, Map<String, Object> params) {
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

    private static String inputStream2String(InputStream is) {
        if (is == null) {
            return null;
        }
        String result = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int index = -1;
            while ((index = is.read(buffer)) != -1) {
                baos.write(buffer, 0, index);
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

    private void doCallback(final String result, final boolean isCache, final Class resultType, final int responseCode, final IResult callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
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

    //拼接请求的url，并将该url作为请求的标识位
    private String createUrl(String url, Map<String, Object> params) {
        if (params == null || params.size() == 0) {
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
        if (builder.indexOf("&") == builder.length() - 1) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    /**
     * 设置网络缓存插件
     **/
    public static final void setHttpCache(IHttpCache cache) {
        DefaultExecuteHandler.cache = cache;
    }

    /**
     * 设置核心线程数
     *
     * @param coreSize
     */
    public static void setCoreSize(int coreSize) {
        threadPool.setCorePoolSize(coreSize);
    }

    /**
     * 设置默认的线程工厂
     *
     * @param factory
     */
    public static void setFactory(ThreadFactory factory) {
        threadPool.setThreadFactory(factory);
    }

    /**
     * 设置最大线程数
     *
     * @param maxSize
     */
    public static void setMaxSize(int maxSize) {
        threadPool.setMaximumPoolSize(maxSize);
    }

    /**
     * 设置空闲线程的最大存活时间（单位：秒）
     *
     * @param aliveTime
     */
    public static void setAliveTime(int aliveTime) {
        threadPool.setKeepAliveTime(aliveTime, TimeUnit.SECONDS);
    }


    /**
     * 设置网络请求最大等待执行时间
     *
     * @param waitTimeout
     */
    public static void setWaitTimeout(int waitTimeout) {
        DefaultExecuteHandler.waitTimeout = waitTimeout;
    }

    /**
     * 设置网络请求最大连接时间
     *
     * @param connectTimeout
     */
    public static void setConnectTimeout(int connectTimeout) {
        DefaultExecuteHandler.connectTimeout = connectTimeout;
    }

    /**
     * 设置提交网络任务的线程池
     *
     * @param threadPool
     */
    public static void setThreadPool(ThreadPoolExecutor threadPool) {
        DefaultExecuteHandler.threadPool = threadPool;
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
