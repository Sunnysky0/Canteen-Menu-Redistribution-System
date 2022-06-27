package cn.sunnysky.security;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.annotation.SideOnly;

import java.lang.reflect.Method;

import static cn.sunnysky.IntegratedManager.logger;

public class SideChecker {
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
}
