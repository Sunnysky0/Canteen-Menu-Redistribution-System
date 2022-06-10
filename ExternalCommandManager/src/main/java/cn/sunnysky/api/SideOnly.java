package cn.sunnysky.api;

import java.lang.annotation.*;

@Inherited
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface SideOnly {
    Side value() default Side.UNKNOWN;
}
