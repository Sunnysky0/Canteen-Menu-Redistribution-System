package cn.sunnysky;

import cn.sunnysky.api.IFileManager;
import cn.sunnysky.api.ILogger;
import cn.sunnysky.api.LogType;
import cn.sunnysky.api.Side;
import cn.sunnysky.api.default_impl.DefaultFileManager;
import cn.sunnysky.api.default_impl.DefaultLogger;
import cn.sunnysky.command.CommandManager;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

public class IntegratedManager {
    public static ILogger logger = new DefaultLogger();
    public static IFileManager fileManager = new DefaultFileManager();
    private final Side currentSide;
    private final CommandManager commandManager;

    public IntegratedManager(Side currentSide) {
        this.currentSide = currentSide;
        commandManager = new CommandManager(this.currentSide);
    }

    public void sendCmd(int id, PrintWriter writer){
        try {
            commandManager.sendCmd(id,writer);
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
            writer.println("[RSP]: No command matches the input!");
        }
    }


}
