package cn.sunnysky.command;

import cn.sunnysky.api.Side;
import cn.sunnysky.api.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;

public abstract class Command {
    public static int ID = 0;

    public Command(int id){
        ID =id;
    }

    /**
     * @author Sunnysky
     * @param writer The output stream from the client. Used for sending formatted message.
     * @param args The arguments used by the method. It can have no elements.
     */
    @SideOnly(value = Side.CLIENT)
    public abstract void onSend(@NotNull PrintWriter writer,String... args);

    /**
     * @author Sunnysky
     * @param args The arguments used by the method for some specific reasons. Normally, it has no elements.
     */
    @SideOnly(value = Side.SERVER)
    public abstract void onReceive(String... args);

}
