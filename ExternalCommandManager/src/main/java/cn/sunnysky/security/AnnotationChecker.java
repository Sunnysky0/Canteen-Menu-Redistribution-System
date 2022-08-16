package cn.sunnysky.security;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.annotation.RequirePermission;
import cn.sunnysky.api.annotation.SideOnly;

import java.lang.reflect.Method;

import static cn.sunnysky.IntegratedManager.logger;
import static cn.sunnysky.user.UserPermission.UNKNOWN;

public class AnnotationChecker {
    public static boolean checkSide(Method method){
        if(method.isAnnotationPresent(SideOnly.class)){
            SideOnly sideOnly = method.getAnnotation(SideOnly.class);
            if(sideOnly.value() == IntegratedManager.currentSide) return true;
            else {
                logger.log("Incorrect invoke of method! A " + sideOnly.value().toString() +
                        " side method can only be invoked from " + sideOnly.value().toString()
                );
            }
        }else {
            logger.log("The method is not annotated");
            return true;
        }
        return false;
    }

    public static boolean checkPermission(Method method, String temporaryUserActivationCode){
        if(method.isAnnotationPresent(RequirePermission.class)){
            RequirePermission permission = method.getAnnotation(RequirePermission.class);
            if((temporaryUserActivationCode == null && permission.value() == UNKNOWN)

                    ||

                    ( temporaryUserActivationCode != null
                            &&
                    permission.value().ordinal()
                    <=
                    IntegratedManager.getUserManager().getUserPermission(
                            temporaryUserActivationCode
                    ).ordinal()
                    ))
                return true;
            else logger.log("An action was blocked due to not enough permission");
        } else {
            logger.log("The method is not annotated");
            return true;
        }

        return false;
    }
}
