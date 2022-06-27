package cn.sunnysky.command;

import cn.sunnysky.api.annotation.Side;
import cn.sunnysky.api.annotation.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;


public abstract class Command {

    /**
     * ID represents the identity code of a particular instance of Command. The default value is 0. So do not use it directly when the instance may not be created.
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
    @SideOnly(value = Side.CLIENT)
    public abstract void onSend(@NotNull PrintWriter writer,String... args);

    /**
     * @author Sunnysky
     * @param args The arguments used by the method for some specific reasons. Normally, it has no elements.
     * @return The response from the server side after or during the runtime of the method.
     */
    @SideOnly(value = Side.SERVER)
    public abstract String onReceive(String... args);

}
