package pk.net.test;

/**
 * @author wzj
 * @version 2015/11/19
 * @Mark
 */
public class Entity {

    public String content;
    public int code;
    public String message;
    public long serverTime;

    @Override
    public String toString() {
        return "Entity{" +
                "content='" + content + '\'' +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", serverTime=" + serverTime +
                '}';
    }

}
