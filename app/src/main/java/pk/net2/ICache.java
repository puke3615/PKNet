package pk.net2;

/**
 * @author zijiao
 * @version 2016/1/15
 * @Mark
 */
public interface ICache {

    void setCache(IRequest request, Response response);

    Response getCache();

}
