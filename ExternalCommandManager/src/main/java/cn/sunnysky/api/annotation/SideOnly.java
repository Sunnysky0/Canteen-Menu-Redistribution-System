package cn.sunnysky.api.annotation;

import java.lang.annotation.*;

@Inherited
@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface SideOnly {
    Side value() default Side.UNKNOWN;
}
