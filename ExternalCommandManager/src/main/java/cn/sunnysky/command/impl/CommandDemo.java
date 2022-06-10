package cn.sunnysky.command.impl;

import cn.sunnysky.api.Side;
import cn.sunnysky.api.SideOnly;
import cn.sunnysky.command.Command;
import cn.sunnysky.command.CommandManager;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;

import static cn.sunnysky.IntegratedManager.logger;

public class CommandDemo extends Command {
    public static final int DEMO_ID = 1000;

    public CommandDemo(){
        super(DEMO_ID);
    }

    @Override
    @SideOnly(value = Side.CLIENT)
    public void onSend(@NotNull PrintWriter writer,String... args) {
        writer.println("CMD-" + DEMO_ID);
    }

    @Override
    @SideOnly(value = Side.SERVER)
    public String onReceive(String... args) {
            logger.log("Demo received");
            return "Method executed successfully!";
    }
}
