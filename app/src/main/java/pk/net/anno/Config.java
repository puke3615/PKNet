package pk.net.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pk.net.core.IRequest;
import pk.net.core.ITask;
import pk.net.core.Type;


/**
 * @author wzj
 * @version 2015-8-26
 * @Mark
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Config {

    /**
     * 请求类型
     **/
    Type type() default Type.POST;

    /**
     * 是否支持缓冲
     **/
    boolean supportCache() default false;

    /**
     * 请求优先级
     */
    IRequest.Priority priority() default IRequest.Priority.Normal;

}
