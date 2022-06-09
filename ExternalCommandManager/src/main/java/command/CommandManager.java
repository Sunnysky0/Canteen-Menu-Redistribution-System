package command;

import api.ILogger;
import api.Side;
import api.SideOnly;
import api.default_impl.DefaultLogger;
import command.impl.CommandDemo;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

public class CommandManager {
    private static ArrayList<Command> Commands;
    private static ILogger logger = new DefaultLogger();
    private final Side currentSide;

    public CommandManager(Side side) {
        currentSide = side;
        init();
    }

    private void init(){
        Commands = new ArrayList<>();
        Commands.add(new CommandDemo());
    }

    public static ILogger getLogger() {
        return logger;
    }

    private boolean checkSide(Method method){
        if(method.isAnnotationPresent(SideOnly.class)){
            SideOnly sideOnly = method.getAnnotation(SideOnly.class);
            if(sideOnly.value() == currentSide) return true;
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

    public void sendCmd(int id, PrintWriter writer) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Command cmd = getCmdById(id);

        assert cmd != null;
        Class<? extends Command> commandClass = cmd.getClass();
        Method onSend = commandClass.getMethod("onSend", PrintWriter.class);

        if(checkSide(onSend)) onSend.invoke(cmd, writer);
    }

    public void resolveCmd(String input) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ArrayList<String> strs = new ArrayList<>();
        Collections.addAll(strs, input.split("-"));
        if(!strs.get(0).contentEquals("CMD")){
            //TODO: Handle exception
        }
        Command cmd = getCmdById(Integer.parseInt(strs.get(1)));

        assert cmd != null;
        Class<? extends Command> commandClass = cmd.getClass();
        Method onReceive = commandClass.getMethod("onReceive", String[].class);

        if(checkSide(onReceive)){
            strs.remove(0);
            strs.remove(1);
            onReceive.invoke(cmd,strs.toArray());
        }
    }

    private Command getCmdById(int id){
        for (Command c:
             Commands) {
            if(c.ID == id) return c;
        }
        return null;
    }

}
