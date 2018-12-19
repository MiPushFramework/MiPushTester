package moe.yuuta.server.formprocessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FormData {
    String value();
    boolean urlEncode() default false;
    boolean ignorable() default true;
}
