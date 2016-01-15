package pk.net2;

/**
 * @author zijiao
 * @version 2016/1/15
 * @Mark
 */
public interface IDispatcherForRequest {

    void finish(Request task);

    HttpConfig getConfig();

}
