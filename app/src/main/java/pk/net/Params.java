/**
 *
 */
package pk.net;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import pk.net.core.IRequest;
import pk.net.core.Type;

/**
 * @author wzj
 * @version 2015年6月3日
 * @Mark Builder模式的参数类
 */
public class Params implements IRequest {

    private String url;

    private Type type = Type.POST;// 默认请求方式为 POST

    private boolean supportCache;

    private Map<String, Object> map = new HashMap<String, Object>();

    public Params(String url) {
        this.url = url;
    }

    /**
     * 添加请求参数
     **/
    public Params append(String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
        return this;
    }


    /**
     * 设置请求方式
     **/
    public Params setType(Type type) {
        this.type = type;
        return this;
    }

    public Params setMap(Map<String, Object> map) {
        this.map = map;
        return this;
    }


    public Params setSupportCache(boolean supportCache) {
        this.supportCache = supportCache;
        return this;
    }

    public Params set(boolean supportCache) {
        this.supportCache = supportCache;
        return this;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Map<String, Object> getParams() {
        return map;
    }

    @Override
    public boolean supportCache() {
        return true;
    }

    @Override
    public Priority getPriority() {
        return Priority.Normal;
    }

}
