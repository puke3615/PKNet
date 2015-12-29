package pk.net.core;

/**
 * @author wzj
 * @version 2015/11/19
 * @Mark
 */
public class ErrorMsg {

    /**
     * 请求响应码
     */
    public int responseCode;

    /**
     * 错误信息描述
     */
    public String desc;

    public ErrorMsg() {
    }

    public ErrorMsg(String desc) {
        this.desc = desc;
    }

    public ErrorMsg(int responseCode) {

        this.responseCode = responseCode;
    }

    public ErrorMsg(int responseCode, String desc) {

        this.responseCode = responseCode;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "ErrorMsg{" +
                "responseCode=" + responseCode +
                ", desc='" + desc + '\'' +
                '}';
    }
}
