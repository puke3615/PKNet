package pk.net2;


/**
 * @author zijiao
 * @version 2016/1/15
 * @Mark
 */
public class HttpConfig {

    ICache mCache;
    IExecute mExecute;
    IResponseCover mResponseCover;

    public HttpConfig configure(ICache cache) {
        this.mCache = cache;
        return this;
    }

    public HttpConfig configure(IExecute execute) {
        this.mExecute = execute;
        return this;
    }

    public HttpConfig configure(IResponseCover responseCover) {
        this.mResponseCover = responseCover;
        return this;
    }

}
