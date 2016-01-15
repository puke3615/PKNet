package pk.net2.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import pk.net2.IExecute;
import pk.net2.IRequest;
import pk.net2.Response;

/**
 * @author zijiao
 * @version 2016/1/15
 * @Mark
 */
public class DefaultExecute implements IExecute {

    private static int waitTimeout = 10000;
    private static int connectTimeout = 5000;

    @Override
    public Response getResponse(IRequest request) {
        String url = request.getUrl();
        IRequest.Method type = request.getMethod();
        Map<String, String> params = request.getParams();
        String urlFlag = createUrl(url, params);
        Response response = null;
        switch (type) {
            case GET:
                response = requestGet(urlFlag);
                break;
            case POST:
                response = requestPost(url, params);
                break;
        }
        return response;
    }

    //拼接请求的url，并将该url作为请求的标识位
    private String createUrl(String url, Map<String, String> params) {
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
    private static Response requestPost(String u, Map<String, String> params) {
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

}
