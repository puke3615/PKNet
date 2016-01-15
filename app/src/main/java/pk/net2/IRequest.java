package pk.net2;

import java.util.Map;

/**
 * @author zijiao
 * @version 2016/1/15
 * @Mark
 */
public interface IRequest {

    String getUrl();

    Map<String, String> getParams();

    Method getMethod();

    Map<String, String> getHeader();

    public enum Method {
        GET,
        POST,
        HEAD,
        PUT,
        DELETE
    }

}
