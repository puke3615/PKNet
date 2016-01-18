package pk.net;

/**
 * @author wzj
 * @version 2015-8-25
 * @Mark
 */
public class PKException extends RuntimeException {

	private static final long serialVersionUID = 7415812799044695127L;

	public PKException() {
		super();
	}

	public PKException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public PKException(String detailMessage) {
		super(detailMessage);
	}

	public PKException(Throwable throwable) {
		super(throwable);
	}

}
