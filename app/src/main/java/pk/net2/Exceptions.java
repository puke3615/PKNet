package pk.net2;

/**
 * @author zijiao
 * @version 2016/1/15
 * @Mark
 */
public class Exceptions {

    public static void n(String msg) {
        throw new NullPointerException(msg);
    }

    public static void r(String msg) {
        throw new RuntimeException(msg);
    }

    public static void i(String msg) {
        throw new IllegalArgumentException(msg);
    }

    public static void noSupport(String msg) {
        throw new NoSupportException(msg);
    }

}
