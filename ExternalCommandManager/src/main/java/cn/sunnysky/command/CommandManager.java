package cn.sunnysky.command;

import cn.sunnysky.api.annotation.Side;
import cn.sunnysky.api.default_impl.DefaultClassUtil;
import cn.sunnysky.command.impl.*;
import cn.sunnysky.security.AnnotationChecker;
import cn.sunnysky.util.ClassUtil;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static cn.sunnysky.IntegratedManager.logger;

public class CommandManager {

    private static ArrayList<Command> Commands;
    private final Side currentSide;
    private static ClassUtil classUtil = new DefaultClassUtil();

    public CommandManager(Side side) {
        currentSide = side;
        init();
    }

    public static void setClassUtil(ClassUtil classUtil) {
        CommandManager.classUtil = classUtil;
    }

    private void init(){
        Commands = new ArrayList<>();

        try {
            loadCommands("cn.sunnysky.command.impl");
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            logger.log("Initialization failed");
        }
    }

    private void loadCommands(String packageName)
            throws InstantiationException, IllegalAccessException {
        List<Class<?>> clz = null;
        try {
            clz = Collections.unmodifiableList(classUtil.getClassesForPackage(packageName));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            logger.log("Exception while loading commands");
            throw new InstantiationException();
        }

        for (Class<?> c : clz){
            if (c.getSuperclass() == Command.class)
                register((Command) c.newInstance());
        }

        logger.log("Loaded " + Commands.size() + " commands signed from " + packageName);
    }

    public static ArrayList<Command> getCommands() {
        return Commands;
    }


    public void register(Command cmd){ Commands.add(cmd); }

    public void sendCmd(int id, PrintWriter writer,String... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Command cmd = getCmdById(id);

        assert cmd != null;
        Class<? extends Command> commandClass = cmd.getClass();

        Method onSend = commandClass.getMethod("onSend", PrintWriter.class,String[].class);

        if(AnnotationChecker.checkSide(onSend)) cmd.onSend(writer, args);
    }

    public void resolveCmd(String input,PrintWriter writer) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException,NullPointerException {
        String[] strs = input.split(";");

        String id = null;
        String temporaryUserActivationCode = null;
        String[] args = null;
        for(String str:strs){
            if(str.startsWith("CMD")){
                id=str.split(":")[1];
            } else if (str.startsWith("ARGS")){
                String[] strings = str.split(":");
                if ( strings.length > 1)
                    args= strings[1].split(",");
            } else if (str.startsWith("AUTH")) {
                String[] strings = str.split(":");
                if (strings.length > 1)
                    temporaryUserActivationCode = str.split(":")[1];
            }
        }

        assert id != null;
        Command cmd;
        try {
            cmd = getCmdById(Integer.parseInt(id));
        } catch (NumberFormatException e){
            logger.log("Command Id not solved");
            return;
        }

        Class<? extends Command> commandClass = cmd.getClass();
        Method onReceive = commandClass.getMethod("onReceive", String[].class);

        if(AnnotationChecker.checkSide(onReceive)){
            if(! AnnotationChecker.checkPermission(onReceive,temporaryUserActivationCode)) {
                writer.println("ERR: Not enough authority");
                return;
            }
            writer.println(cmd.onReceive(args));
        }
    }

    @Nullable
    private Command getCmdById(int id){
        for (Command c:
             Commands) {
            if(c.ID == id) return c;
        }
        return null;
    }

}
