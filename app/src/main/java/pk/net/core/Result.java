package pk.net.core;

/**
 * @author wzj
 * @version 2015/11/19
 * @Mark
 */
public class Result<T> {

    private T body;

    private String errorMsg;

    private int responseCode;

    private boolean isCache;

    public static Result create(Object body, String errorMsg, int responseCode, boolean isCache) {
        Result result = new Result();
        result.body = body;
        result.errorMsg = errorMsg;
        result.responseCode = responseCode;
        result.isCache = isCache;
        return result;
    }

    public static Result createError(String errorMsg, int responseCode) {
        return create(null, errorMsg, responseCode, false);
    }

    public static Result createError(String errorMsg) {
        return createError(errorMsg, 0);
    }

    public boolean isSuccess() {
        return responseCode == 200 && errorMsg == null;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public boolean isCache() {
        return isCache;
    }

    public void setIsCache(boolean isCache) {
        this.isCache = isCache;
    }

    @Override
    public String toString() {
        return "Result{" +
                "body=" + body +
                ", errorMsg='" + errorMsg + '\'' +
                ", responseCode=" + responseCode +
                ", isCache=" + isCache +
                '}';
    }
}
