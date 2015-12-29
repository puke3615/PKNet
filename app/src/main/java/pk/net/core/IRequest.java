package pk.net.core;

import java.util.Map;


/**
 * @author wzj
 * @version 2015-8-24
 * @Mark 请求接口
 */
public interface IRequest {

	String getUrl();

	Type getType();

	Map<String, Object> getParams();

	boolean supportCache();

}
