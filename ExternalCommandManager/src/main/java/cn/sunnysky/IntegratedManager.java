package cn.sunnysky;

import cn.sunnysky.api.IFileManager;
import cn.sunnysky.api.ILogger;
import cn.sunnysky.api.LogType;
import cn.sunnysky.api.annotation.Side;
import cn.sunnysky.api.annotation.SideOnly;
import cn.sunnysky.api.default_impl.DefaultFileManager;
import cn.sunnysky.api.default_impl.DefaultLogger;
import cn.sunnysky.command.CommandManager;
import cn.sunnysky.security.SideChecker;
import cn.sunnysky.user.UserManager;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Predicate;

import static cn.sunnysky.security.SideChecker.checkSide;

public class IntegratedManager {
    public static ILogger logger = new DefaultLogger();
    public static IFileManager fileManager;
    public static Side currentSide = Side.UNKNOWN;
    public static UserManager.User currentUser = null;
    private final CommandManager commandManager;
    private static UserManager userManager;


    public IntegratedManager(Side currentSide) {
        this.currentSide = currentSide;
        commandManager = new CommandManager(this.currentSide);
        fileManager = new DefaultFileManager("DATA_OF_" + this.currentSide.toString());
        userManager = new UserManager();
    }

    public void sendCmd(int id, PrintWriter writer,String... args){
        try {
            commandManager.sendCmd(id,writer,args);
        } catch (NoSuchMethodException
                | InvocationTargetException
                | IllegalAccessException e) {
            logger.log(e.toString());
        }
    }

    public void resolveCmd(String input, PrintWriter writer){
        try {
            commandManager.resolveCmd(input, writer);
        } catch (NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException e) {
            logger.log(e.toString());
            writer.println(logger.getFormattedLog("Something went wrong unexpectedly", LogType.ERROR));
        } catch (NullPointerException e){
            e.printStackTrace(writer);
        }
    }

    public void initializeCurrentUser(){
        try {
            Method method = UserManager.class.getMethod("getDefaultUser");
            if(checkSide(method)) currentUser = (UserManager.User) method.invoke(userManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static final UserManager getUserManager(){ return userManager; }

}
