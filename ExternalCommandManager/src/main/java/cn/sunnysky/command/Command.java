package cn.sunnysky.command;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.annotation.Side;
import cn.sunnysky.api.annotation.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;


public abstract class Command {

    /**
     * ID represents the identity code of a particular instance of Command.
     * @author Sunnysky
     */
    public final int ID;

    public Command(int id){
        ID =id;
    }

    /**
     * @author Sunnysky
     * @param writer The output stream from the client. Used for sending formatted message.
     * @param args The arguments used by the method. It can have no elements.
     */
    @SuppressWarnings("NewApi")
    @SideOnly(value = Side.CLIENT)
    public void onSend(@NotNull PrintWriter writer,String... args){
        StringBuilder s = new StringBuilder("CMD:" + this.ID + ";" + "ARGS:");

        for (String arg : args) s.append(arg).append(",");

        s.deleteCharAt(s.length() - 1);

        if (IntegratedManager.getTemporaryUserActivationCode() != null)
            s.append(";AUTH:").append(IntegratedManager.getTemporaryUserActivationCode());

        writer.println(s);
    };

    /**
     * @author Sunnysky
     * @param args The arguments used by the method for some specific reasons. Normally, it has no elements.
     * @return The response from the server side after or during the runtime of the method.
     */
    @SideOnly(value = Side.SERVER)
    public abstract String onReceive(String... args);

    @Override
    public String toString() {
        return "CMD: " + ID;
    }
}
