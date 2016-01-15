package pk.net2;

import java.util.Map;

/**
 * @author zijiao
 * @version 2016/1/15
 * @Mark
 */
public class Response<T> {

    private int error;
    private boolean isCache;
    private int responseCode;
    private Map<String, String> headers;
    private String result;
    private T finalResult;

    public Response(String result, int responseCode) {
        this.result = result;
        this.responseCode = responseCode;
    }

    public boolean isSuccess() {
        return responseCode == 200 && error != -1;
    }

    public void set(T t) {
        finalResult = t;
    }

    public T get() {
        return finalResult;
    }

    public int error() {
        return error;
    }

    public void setResponseCode(int code) {
        this.responseCode = code;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeader() {
        return headers;
    }

    public void setCache(boolean cache) {
        this.isCache = cache;
    }

    public boolean isCache() {
        return isCache;
    }
    public void setError(int error) {
        this.error = error;
    }

    public static class Error {
        public static final int NO_SUPPORT = 0;
        public static final int NET_ERROR = 1;
        public static final int COVER_ERROR = 2;
    }

    @Override
    public String toString() {
        return "Response{" +
                "error=" + error +
                ", isCache=" + isCache +
                ", responseCode=" + responseCode +
                ", headers=" + headers +
                ", result='" + result + '\'' +
                ", finalResult=" + finalResult +
                '}';
    }
}
