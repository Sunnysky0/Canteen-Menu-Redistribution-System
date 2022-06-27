package cn.sunnysky.api.annotation;

import cn.sunnysky.user.UserPermission;

import java.lang.annotation.*;

import static cn.sunnysky.user.UserPermission.UNKNOWN;

@Inherited
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    UserPermission value() default UNKNOWN;
}
