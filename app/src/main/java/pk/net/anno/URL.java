/**
 *
 */
package pk.net.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wzj
 * @version 2015-4-15
 * @Mark <b>接口注解器</b><br>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface URL {

    /**
     * 该类(方法)对应的url地址 片段
     **/
    String value();


}
