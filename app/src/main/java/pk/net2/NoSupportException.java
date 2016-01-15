package pk.net2;

/**
 * @author zijiao
 * @version 2016/1/15
 * @Mark
 */
public class NoSupportException extends PKException {
    public NoSupportException() {
    }

    public NoSupportException(String detailMessage) {
        super(detailMessage);
    }

    public NoSupportException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NoSupportException(Throwable throwable) {
        super(throwable);
    }
}
