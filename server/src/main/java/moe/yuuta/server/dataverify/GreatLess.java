package moe.yuuta.server.dataverify;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ask the target field to obey the following rules. It it not obeys, the result will become fail.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = GreatLessGroup.class)
public @interface GreatLess {
    int GREATER = 1;
    int LESSER = 1<<1;
    int EQUAL = 1<<2;
    long value ();
    int flags ();
}
