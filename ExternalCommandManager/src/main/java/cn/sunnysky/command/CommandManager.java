package cn.sunnysky.command;

import cn.sunnysky.api.annotation.Side;
import cn.sunnysky.command.impl.CommandDemo;
import cn.sunnysky.command.impl.CommandDisconnect;
import cn.sunnysky.command.impl.CommandLogin;
import cn.sunnysky.command.impl.CommandRegister;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

import static cn.sunnysky.security.AnnotationChecker.checkPermission;
import static cn.sunnysky.security.AnnotationChecker.checkSide;

public class CommandManager {
    private static ArrayList<Command> Commands;
    private final Side currentSide;

    public CommandManager(Side side) {
        currentSide = side;
        init();
    }

    private void init(){
        Commands = new ArrayList<>();
        Commands.add(new CommandDemo());
        Commands.add( new CommandLogin());
        Commands.add( new CommandRegister());
        Commands.add( new CommandDisconnect());
    }

    public void registerCmd(Command cmd){ Commands.add(cmd); }

    public void sendCmd(int id, PrintWriter writer,String... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Command cmd = getCmdById(id);

        assert cmd != null;
        Class<? extends Command> commandClass = cmd.getClass();
        Method onSend = commandClass.getMethod("onSend", PrintWriter.class,String[].class);

        if(checkSide(onSend)) cmd.onSend(writer, args);
    }

    public void resolveCmd(String input,PrintWriter writer) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException,NullPointerException {
        ArrayList<String> strs = new ArrayList<>();
        Collections.addAll(strs, input.split(";"));
        String id = null;
        String temporaryUserActivationCode = null;
        String[] args = null;
        for(String str:strs){
            if(str.startsWith("CMD")){
                id=str.split(":")[1];
            } else if (str.startsWith("ARGS")){
                args=str.split(":")[1].split(",");
            } else if (str.startsWith("AUTH"))
                temporaryUserActivationCode=str.split(":")[1];
        }

        assert id != null;
        Command cmd = getCmdById(Integer.parseInt(id));

        Class<? extends Command> commandClass = cmd.getClass();
        Method onReceive = commandClass.getMethod("onReceive", String[].class);

        if(checkSide(onReceive)){
            if(! checkPermission(onReceive,temporaryUserActivationCode)) {
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
