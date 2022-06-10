package cn.sunnysky.api;

import java.lang.annotation.*;

@Inherited
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface SideOnly {

    /**
     * @apiNote Used to sign a method with a specific side ( CLIENT, SERVER or UNKNOWN ).
     * @author Sunnysky
     */
    Side value() default Side.UNKNOWN;
}
