package pk.net;

/**
 * @author wzj
 * @version 2015-8-25
 * @Mark
 */
public class PKHttpException extends RuntimeException {

	private static final long serialVersionUID = 7415812799044695127L;

	public PKHttpException() {
		super();
	}

	public PKHttpException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public PKHttpException(String detailMessage) {
		super(detailMessage);
	}

	public PKHttpException(Throwable throwable) {
		super(throwable);
	}

}
