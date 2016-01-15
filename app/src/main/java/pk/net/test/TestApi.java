package pk.net.test;

import pk.net.anno.Config;
import pk.net.anno.Param;
import pk.net.anno.URL;
import pk.net.core.IResult;
import pk.net.core.Type;
import pk.net.core.IRequest;

/**
 * @author wzj
 * @version 2015/11/19
 * @Mark
 */
@URL("http://test.jldo2o.com/homelifeinterface/product/getProducts.do")
public interface TestApi {

    @Config(type = Type.POST, supportCache = true)
    IRequest testRequest(
            @Param("username") String u,
            @Param("password") String p,
            IResult<Entity> result);

}
