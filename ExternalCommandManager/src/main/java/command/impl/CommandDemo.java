package command.impl;

import command.Command;
import command.CommandManager;

import java.io.PrintWriter;

public class CommandDemo extends Command {
    public static final int DEMO_ID = 1000;

    public CommandDemo(){
        super(DEMO_ID);
    }

    @Override
    public void onSend(PrintWriter writer) {
        writer.println("CMD-" + DEMO_ID);
    }

    @Override
    public void onReceive(String... args) {
            CommandManager.getLogger().log("Demo received");
    }
}
