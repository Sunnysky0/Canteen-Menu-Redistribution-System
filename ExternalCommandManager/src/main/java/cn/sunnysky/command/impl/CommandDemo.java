package cn.sunnysky.command.impl;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.annotation.RequirePermission;
import cn.sunnysky.api.annotation.Side;
import cn.sunnysky.api.annotation.SideOnly;
import cn.sunnysky.command.Command;
import cn.sunnysky.user.UserPermission;
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
        String s = "CMD:" + DEMO_ID + ";" + "ARGS:NULL";
        if (IntegratedManager.temporaryUserActivationCode != null)
            s += ";AUTH:" + IntegratedManager.temporaryUserActivationCode;
        writer.println(s);
    }

    @Override
    @SideOnly(value = Side.SERVER)
    @RequirePermission(value = UserPermission.OP)
    public String onReceive(String... args) {
            logger.log("Demo received");
            return "Command executed successfully!";
    }
}
