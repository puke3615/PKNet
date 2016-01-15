package pk.net2;

/**
 * @author zijiao
 * @version 2016/1/15
 * @Mark
 */
public class PKException extends RuntimeException {
    public PKException() {
    }

    public PKException(String detailMessage) {
        super(detailMessage);
    }

    public PKException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public PKException(Throwable throwable) {
        super(throwable);
    }
}
