package cn.sunnysky;

import cn.sunnysky.api.IFileManager;
import cn.sunnysky.api.ILogger;
import cn.sunnysky.api.LogType;
import cn.sunnysky.api.annotation.Side;
import cn.sunnysky.api.default_impl.DefaultFileManager;
import cn.sunnysky.api.default_impl.DefaultLogger;
import cn.sunnysky.command.CommandManager;
import cn.sunnysky.user.UserManager;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static cn.sunnysky.user.security.AnnotationChecker.checkSide;

public class IntegratedManager {
    public static ILogger logger = new DefaultLogger();
    public static IFileManager fileManager;
    public static Side currentSide = Side.UNKNOWN;
    public static String temporaryUserActivationCode = null;
    private final CommandManager commandManager;
    private static UserManager userManager;


    @SuppressWarnings("Client-side constructor")
    public IntegratedManager(Side currentSide) {
        this.currentSide = currentSide;
        commandManager = new CommandManager(this.currentSide);
        if(fileManager instanceof DefaultFileManager || fileManager == null)
            fileManager = new DefaultFileManager("DATA_OF_" + this.currentSide.toString());
    }

    @SuppressWarnings("Server-side constructor")
    public IntegratedManager(Side currentSide,UserManager userManager) {
        this.currentSide = currentSide;
        commandManager = new CommandManager(this.currentSide);
        if( fileManager == null )
            fileManager = new DefaultFileManager("DATA_OF_" + this.currentSide.toString());
        this.userManager = userManager;
    }

    public static void setLogger(ILogger logger) {
        IntegratedManager.logger = logger;
    }

    public static void setFileManager(IFileManager fileManager) {
        IntegratedManager.fileManager = fileManager;
    }

    public void sendCmd(int id, PrintWriter writer, String... args){
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
            writer.println("ERR: Something went wrong unexpectedly");
        } catch (NullPointerException e){
            e.printStackTrace(writer);
        }
    }



    public static final UserManager getUserManager(){ return userManager; }

}
