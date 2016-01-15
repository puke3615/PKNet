package pk.net2;

import java.util.Map;

/**
 * @author zijiao
 * @version 2016/1/15
 * @Mark
 */
class BaseRequest<T extends BaseRequest> implements IRequest {

    private String url;
    private Method method = Method.GET;
    private Map<String, String> params;
    private Map<String, String> header;

    public T setUrl(String url) {
        this.url = url;
        return (T) this;
    }

    public T setMethod(Method method) {
        this.method = method;
        return (T) this;
    }

    public T setParams(Map<String, String> params) {
        this.params = params;
        return (T) this;
    }

    public T setHeader(Map<String, String> header) {
        this.header = header;
        return (T) this;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Map<String, String> getHeader() {
        return header;
    }
}
