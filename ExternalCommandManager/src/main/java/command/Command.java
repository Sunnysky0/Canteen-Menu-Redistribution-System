package command;

import api.Side;
import api.SideOnly;

import java.io.PrintWriter;

public abstract class Command {
    public static int ID = 0;

    public Command(int id){
        ID =id;
    }

    @SideOnly(value = Side.CLIENT)
    public abstract void onSend(PrintWriter writer);

    @SideOnly(value = Side.SERVER)
    public abstract void onReceive(String... args);

}
